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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edu.uncc.assessment05.R;
import edu.uncc.assessment05.databinding.FragmentNftsBinding;
import edu.uncc.assessment05.databinding.NftRowItemBinding;
import edu.uncc.assessment05.models.Nft;
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
    FragmentNftsBinding binding;

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


        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NftAdapter();
        binding.recyclerView.setAdapter(adapter);






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

                            Log.d("demok", "onResponse: "+nft);


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
                    Log.d("demok", "onResponse Failure: "+response.toString());
                }
            }
        });


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
    }
}