package edu.uncc.assessment05.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.uncc.assessment05.R;
import edu.uncc.assessment05.databinding.FragmentFavoritesBinding;
import edu.uncc.assessment05.databinding.NftFavoriteRowItemBinding;
import edu.uncc.assessment05.models.Nft;
import edu.uncc.assessment05.models.NftFavorite;

public class FavoritesFragment extends Fragment {
    public FavoritesFragment() {
        // Required empty public constructor
    }

    ArrayList<NftFavorite> nftFavorites = new ArrayList<>();

    FirebaseFirestore db;

    FirebaseAuth auth;

    FirebaseUser user;
    FragmentFavoritesBinding binding;

    NftFavAdapter adapter;

    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Favorites");

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new NftFavAdapter();
        binding.recyclerView.setAdapter(adapter);

        getFav();

    }

    public void getFav() {

        db.collection("favorites")
                .whereEqualTo("ownerId", user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        nftFavorites.clear();
                        for (QueryDocumentSnapshot documentSnapshot : value) {
                            NftFavorite nftFavorite = new NftFavorite();
                            nftFavorite.name = documentSnapshot.getString("name");
                            nftFavorite.collection_name = documentSnapshot.getString("name");
                            nftFavorite.ownerId = documentSnapshot.getString("ownerId");
                            nftFavorite.docId = documentSnapshot.getString("docId");
                            nftFavorite.image_thumbnail_url = documentSnapshot.getString("image_thumbnail_url");
                            nftFavorite.collection_banner_image_url = documentSnapshot.getString("collection_banner_image_url");
                            nftFavorite.created_at = documentSnapshot.getTimestamp("created_at");
                            nftFavorite.setNftId(documentSnapshot.getString("nftId"));

                            nftFavorites.add(nftFavorite);
                        }



                        adapter.notifyDataSetChanged();

                    }
                });


    }

    class NftFavAdapter extends RecyclerView.Adapter<NftFavAdapter.NftFavViewHolder> {
        @NonNull
        @Override
        public NftFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            NftFavoriteRowItemBinding itemBinding = NftFavoriteRowItemBinding.inflate(getLayoutInflater(), parent, false);

            return new NftFavViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(NftFavViewHolder holder, int position) {
            holder.setUpUI(nftFavorites.get(position));
        }

        @Override
        public int getItemCount() {
            return nftFavorites.size();
        }

        class NftFavViewHolder extends RecyclerView.ViewHolder {
            NftFavoriteRowItemBinding nftFavoriteRowItemBinding;

            NftFavorite nftFavorite;

            public NftFavViewHolder(@NonNull NftFavoriteRowItemBinding itemBinding) {
                super(itemBinding.getRoot());
                nftFavoriteRowItemBinding = itemBinding;

            }

            public void setUpUI(NftFavorite nftFavorite) {

                this.nftFavorite = nftFavorite;
                nftFavoriteRowItemBinding.textViewNftName.setText(this.nftFavorite.getName());
                nftFavoriteRowItemBinding.textViewCollectionName.setText(this.nftFavorite.getCollection_name());
                Picasso.get().load(this.nftFavorite.getCollection_banner_image_url()).into(nftFavoriteRowItemBinding.imageViewCollectionBanner);
                Picasso.get().load(this.nftFavorite.getImage_thumbnail_url()).into(nftFavoriteRowItemBinding.imageViewNftIcon);
                Date date = this.nftFavorite.getCreated_at().toDate();
                SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd K:mm a");
                nftFavoriteRowItemBinding.textViewDateFavored.setText("Favorited On: "+df.format(date));

                nftFavoriteRowItemBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.collection("favorites")
                                .whereEqualTo("docId",nftFavorite.getDocId())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
                                    }
                                });
                    }
                });


            }
        }
    }

}