package com.bentekkie.nextbuslib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

public class TripAdapter  extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    final boolean inifinite;


    private ArrayList<StopTime> trips = new ArrayList<>();

    public class TripViewHolder extends RecyclerView.ViewHolder {

        TextView route;
        TextView time;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            route = itemView.findViewById(routeID);
            time = itemView.findViewById(timeID);
        }

        public void bind(final StopTime item, BiConsumer<View, StopTime> listener) {
            route.setText(itemView.getContext().getString(R.string.trip, item.getRoute(), item.getHeadSign()));
            time.setText(item.getDeparture());
        }
    }

    private final int routeID;
    private final int timeID;
    private final int rowLayoutID;

    private final BiConsumer<View, StopTime> onClickListener;

    public TripAdapter(int routeID, int timeID, int rowLayoutID, BiConsumer<View, StopTime> onClickListener, boolean infinite) {
        this.routeID = routeID;
        this.timeID = timeID;
        this.rowLayoutID = rowLayoutID;
        this.onClickListener = onClickListener;
        this.inifinite = infinite;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayoutID, parent, false);
        return new TripViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.bind(trips.get(position % trips.size()), onClickListener);
    }

    @Override
    public int getItemCount() {
        return inifinite && trips.size() > 0 ? Integer.MAX_VALUE : trips.size();
    }



    public void addAll(Collection<StopTime> newTrips) {
        trips.addAll(newTrips);
        notifyDataSetChanged();
    }

    public void clear() {
        trips.clear();
        notifyDataSetChanged();
    }
}
