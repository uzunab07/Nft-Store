package edu.uncc.assessment05.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.assessment05.R;
import edu.uncc.assessment05.databinding.FragmentNftsBinding;
import edu.uncc.assessment05.databinding.NftRowItemBinding;
import edu.uncc.assessment05.models.Nft;
import edu.uncc.assessment05.models.NftFavorite;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NftsFragment extends Fragment {
    public NftsFragment() {
        // Required empty public constructor
    }
    ArrayList<Nft> nfts = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    FirebaseFirestore db;
    FragmentNftsBinding binding;

    FirebaseUser user;

    NftAdapter adapter;
    private OkHttpClient client = new OkHttpClient();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNftsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("NFTs");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NftAdapter();
        
        binding.recyclerView.setAdapter(adapter);

//        Retrieving Nft from the Api and Displaying them
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/nfts-assets")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("demok", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()){
                    ResponseBody body  = response.body();
                    String responseString = body.string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONArray assets  = jsonObject.getJSONArray("assets");

                        for (int i=0;i<assets.length();i++) {

                            JSONObject asset = assets.getJSONObject(i);
                            JSONObject nftObject = asset.getJSONObject("nft");
                            JSONObject collectionObject = asset.getJSONObject("collection");

                            Nft nft = new Nft();
                            nft.name = nftObject.getString("name");
                            nft.collection_name = collectionObject.getString("name");
                            nft.id = nftObject.getString("id");
                            nft.image_thumbnail_url = nftObject.getString("image_thumbnail_url");
                            nft.collection_banner_image_url = collectionObject.getString("banner_image_url");
                            nfts.add(nft);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    Toast.makeText(getContext(), response.message().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

//    Loading Favorites
getFav();




    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout_item) {
            mListener.logout();
            return true;
        } else if (item.getItemId() == R.id.goto_favorites) {
            //  Viewing Favorites
            mListener.goToFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    NftsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NftsListener) {
            mListener = (NftsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NftsListener");
        }
    }
    public void getFav(){
        db.collection("favorites")
                .whereEqualTo("ownerId",user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots ) {
                            Log.d("TAG", "onSuccess: "+documentSnapshot.toString());
                        }
                    }
                });
    }

     class NftAdapter extends RecyclerView.Adapter<NftAdapter.NftViewHolder> {

        public  class NftViewHolder extends RecyclerView.ViewHolder{
            NftRowItemBinding binding;
            Nft nft;
            public NftViewHolder(@NonNull NftRowItemBinding itemBinding) {
                super(itemBinding.getRoot());
                binding = itemBinding;
            }

            public void setupUi(Nft item){
                this.nft = item;

                binding.textViewNftName.setText(nft.name);
                binding.textViewCollectionName.setText(nft.collection_name);
                Picasso.get().load(nft.collection_banner_image_url).into(binding.imageViewCollectionBanner);
                Picasso.get().load(nft.image_thumbnail_url).into(binding.imageViewNftIcon);


//                TODO Adding to favorites

                
                DocumentReference docref = db.collection("favorites").document();
                binding.imageViewAddRemoveFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NftFavorite nftFavorite = new NftFavorite();

//                      Create Nft Favorite for Database
                        nftFavorite.setCollection_banner_image_url(nft.getCollection_banner_image_url());
                        nftFavorite.setImage_thumbnail_url(nft.getImage_thumbnail_url());
                        nftFavorite.setOwnerId(user.getUid());
                        nftFavorite.setCollection_name(nft.getCollection_name());
                        nftFavorite.setDocId(docref.getId());
                        nftFavorite.setCreated_at(Timestamp.now());
                        nftFavorite.setName(nft.getName());


                        if(binding.imageViewAddRemoveFavorite.getDrawable().getConstantState() ==
                         getResources().getDrawable(R.drawable.ic_heart_full).getConstantState()){
                            binding.imageViewAddRemoveFavorite.setImageResource(R.drawable.ic_heart_empty);
                            db.collection("favorites")
                                    .whereEqualTo("ownerId",nftFavorite.getOwnerId())
                                    .whereEqualTo("docId",nftFavorite.getDocId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            queryDocumentSnapshots.getDocuments().get(0).getReference().delete();

                                        }
                                    });
                        }else{
                            binding.imageViewAddRemoveFavorite.setImageResource(R.drawable.ic_heart_full);
                            docref.set(nftFavorite)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getActivity(), nftFavorite.getName()+" is added to Fav ", Toast.LENGTH_SHORT).show();
                                            Log.d("demok", "onComplete: "+nft.getCollection_name());
                                        }
                                    });

                        }



                    }
                });

            }


        }



        @NonNull
        @Override
        public NftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            NftRowItemBinding itemBinding = NftRowItemBinding.inflate(getLayoutInflater(),parent,false);
            return new NftViewHolder(itemBinding);
        }


        @Override
        public void onBindViewHolder(NftViewHolder holder, int position) {
                    holder.setupUi(nfts.get(position));
        }

        @Override
        public int getItemCount() {
            return nfts.size();
        }
    }


    public interface NftsListener {
        void logout();
        void goToFavorite();
    }
}