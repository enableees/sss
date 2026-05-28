package com.example.kurstaskmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurstaskmanager.data.BlockedItem;

import java.util.ArrayList;
import java.util.List;

public class BlockedAdapter extends RecyclerView.Adapter<BlockedAdapter.BlockedViewHolder> {

    private List<BlockedItem> items = new ArrayList<>();
    private OnBlockedClickListener clickListener;
    private OnBlockedLongClickListener longClickListener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnBlockedClickListener {
        void onBlockedClick(BlockedItem item);
    }

    public interface OnBlockedLongClickListener {
        void onBlockedLongClick(BlockedItem item);
    }

    public void setOnBlockedClickListener(OnBlockedClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnBlockedLongClickListener(OnBlockedLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setItems(List<BlockedItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        int old = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        notifyItemChanged(old);
    }

    @NonNull
    @Override
    public BlockedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blocked, parent, false);
        return new BlockedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedViewHolder holder, int position) {
        BlockedItem item = items.get(position);
        holder.bind(item, position, clickListener, longClickListener, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class BlockedViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvType, tvName, tvStatus;

        public BlockedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvBlockedNumber);
            tvType = itemView.findViewById(R.id.tvBlockedType);
            tvName = itemView.findViewById(R.id.tvBlockedName);
            tvStatus = itemView.findViewById(R.id.tvBlockedStatus);
        }

        public void bind(BlockedItem item, int position,
                         OnBlockedClickListener clickListener,
                         OnBlockedLongClickListener longClickListener,
                         boolean isSelected) {
            tvNumber.setText("#" + (position + 1));
            tvType.setText(item.getType() == 0 ? "Приложение" : "Сайт");
            tvName.setText(item.getName());
            tvStatus.setText(item.isActive() ? "Активна" : "Неактивна");

            itemView.setSelected(isSelected);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onBlockedClick(item);
            });

            itemView.setOnLongClickListener(v -> {
                int prev = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(prev);
                notifyItemChanged(selectedPosition);
                if (longClickListener != null) longClickListener.onBlockedLongClick(item);
                return true;
            });
        }
    }
}