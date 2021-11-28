package com.example.zpi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.adapters.PhotoInTripAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentSingleTripBinding;
import com.example.zpi.models.MultimediaFile;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PhotoDao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SingleTripFragment extends Fragment {

    public static final String TRIP_KEY="TRIP_KEY";
    FragmentSingleTripBinding binding;
    private Trip currTrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentSingleTripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.btnParticipants.setOnClickListener(c->showParticipants());
        binding.btnForum.setOnClickListener(c->showForum());

        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");

        ProgressDialog progressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Pobieranie danych...");
        getActivity().runOnUiThread(() -> progressDialog.show());

        new Thread(() -> {
            try {
                PhotoDao photoDao = new PhotoDao(BaseConnection.getConnectionSource());
                List<MultimediaFile> photos = photoDao.getPhotosFromTrip(currTrip);
                if(photos != null && photos.size() > 0) {
                    List<Bitmap> bitmaps = new ArrayList<>();
                    for (MultimediaFile multimediaFile : photos) {
                        if (multimediaFile.getPhoto()) {
                            Bitmap bitmap = null;
                            try {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL(multimediaFile.getUrl()).getContent());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            multimediaFile.setBitmap(bitmap);
                        } else {
                            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                            mediaMetadataRetriever.setDataSource(multimediaFile.getUrl(), new HashMap<String, String>());
                            Bitmap thumb = mediaMetadataRetriever.getFrameAtTime();
                            multimediaFile.setBitmap(thumb);

                        }
                    }

                    getActivity().runOnUiThread(() -> {
                        binding.tvNoPhotos.setVisibility(View.GONE);
                        binding.rvPhotos.setVisibility(View.VISIBLE);

                        PhotoInTripAdapter photoAdapter = new PhotoInTripAdapter(photos);
                        photoAdapter.setOnItemClickListener((position, v) -> showImage(position));
                        binding.rvPhotos.setAdapter(photoAdapter);
                        LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        binding.rvPhotos.setLayoutManager(upcomingLayoutManager);

                        binding.rvPhotos.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);

                        binding.rvPhotos.post(() -> {
                            LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(binding.rvPhotos.getContext()) {

                                @Override
                                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                    return 15;
                                }
                            };

                            linearSmoothScroller.setTargetPosition(Integer.MAX_VALUE - 1);
                            upcomingLayoutManager.startSmoothScroll(linearSmoothScroller);
                        });
                    });
                }

                progressDialog.dismiss();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                progressDialog.dismiss();
            }
        }).start();

        return root;
    }

    public void showParticipants(){
        Bundle bundle=new Bundle();
        bundle.putSerializable(TRIP_KEY, currTrip);
        Navigation.findNavController(getView()).navigate(R.id.action_singleTripFragment_to_tripParticipantsFragment, bundle);
    }

    public void showForum(){
        Intent intent = new Intent(getContext(), ForumListActivity.class);
        intent.putExtra(TRIP_KEY, currTrip);
        startActivity(intent);
    }

    public void showImage(int position){
        Intent intent = new Intent(getContext(), SinglePhotoActivity.class);
        intent.putExtra("TRIP", currTrip);
        intent.putExtra("POSITION", position);
        startActivity(intent);
    }
}