package com.bentekkie.nextbuslib;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiConsumer;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {

    private ArrayList<Stop> stops = new ArrayList<>();

    private final BiConsumer<View, Stop> onClickListener;


    private final boolean inifinite;
    private final int stopNameID;
    private final int stopCodeID;
    private final int rowLayoutID;


    public class StopViewHolder extends RecyclerView.ViewHolder{

        TextView stopName;
        TextView stopCode;

        public StopViewHolder(@NonNull View itemView) {
            super(itemView);
            stopName = itemView.findViewById(stopNameID);
            stopCode = itemView.findViewById(stopCodeID);
        }

        public void bind(final Stop item) {
            stopName.setText(item.getStopName());
            stopCode.setText(item.getStopCode());
            itemView.setOnClickListener(v -> onClickListener.accept(v, item));
        }
    }


    public StopAdapter(int stopNameId, int stopCodeID, int rowLayoutID, BiConsumer<View, Stop> onClickListener, boolean inifinite) {
        this.stopCodeID = stopCodeID;
        this.stopNameID = stopNameId;
        this.rowLayoutID = rowLayoutID;
        this.onClickListener = onClickListener;
        this.inifinite = inifinite;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayoutID, parent, false);
        return new StopViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        holder.bind(stops.get(position % stops.size()));
    }

    @Override
    public int getItemCount() {
        return inifinite && stops.size() > 0 ? Integer.MAX_VALUE : stops.size();
    }

    public int getActualItemCount(){
        return stops.size();
    }


    public void add(Stop newStops) {
        stops.add(newStops);
        notifyDataSetChanged();
    }
    public void addAll(Collection<Stop> newStops) {
        stops.addAll(newStops);
        notifyDataSetChanged();
    }

    public void sort(Comparator<Stop> comparator) {
        stops.sort(comparator);
        notifyDataSetChanged();
    }

    public void clear() {
        stops.clear();
        notifyDataSetChanged();
    }
}
