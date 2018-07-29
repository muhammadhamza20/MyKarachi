package com.example.muhammadhamza.mykarachi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by MuhammadHamza on 27/06/2018.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private final View window;
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    public void rendowWindowText(Marker marker, View view){
        TextView infoTitle = (TextView) view.findViewById(R.id.title);
        TextView infoSnippet = (TextView) view.findViewById(R.id.snippet);

        String title = marker.getTitle();
        if(!title.equals(""))
            infoTitle.setText(title);

        String snippet = marker.getSnippet();
        if(!snippet.equals(""))
            infoSnippet.setText(snippet);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, window);
        return window;
    }
}
