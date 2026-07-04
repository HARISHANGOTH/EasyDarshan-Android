package com.easydarshan.ui.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.easydarshan.R;
import com.easydarshan.data.model.Temple;
import com.easydarshan.databinding.ItemTempleCardHorizontalBinding;

public class TempleAdapter extends ListAdapter<Temple, TempleAdapter.TempleViewHolder> {

    public interface OnTempleClickListener {
        void onTempleClick(Temple temple);
    }

    private final OnTempleClickListener listener;

    public TempleAdapter(OnTempleClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    // Backward-compatible constructor (null first arg ignored)
    public TempleAdapter(Object ignored, OnTempleClickListener listener) {
        this(listener);
    }

    private static final DiffUtil.ItemCallback<Temple> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Temple>() {
                @Override
                public boolean areItemsTheSame(@NonNull Temple o, @NonNull Temple n) {
                    return o.getId() != null && o.getId().equals(n.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Temple o, @NonNull Temple n) {
                    return o.equals(n);
                }
            };

    @NonNull
    @Override
    public TempleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTempleCardHorizontalBinding binding = ItemTempleCardHorizontalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        // Calculate card width as 78% of screen width for responsive "peek" effect
        android.util.DisplayMetrics metrics = parent.getContext().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int cardWidth = (int) (screenWidth * 0.78f);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) binding.getRoot().getLayoutParams();
        if (params == null) {
            params = new RecyclerView.LayoutParams(cardWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            params.width = cardWidth;
        }
        binding.getRoot().setLayoutParams(params);

        return new TempleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TempleViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    /** Legacy method — delegates to ListAdapter submitList */
    public void updateList(java.util.List<Temple> temples) {
        submitList(temples);
    }

    static class TempleViewHolder extends RecyclerView.ViewHolder {

        private final ItemTempleCardHorizontalBinding b;

        TempleViewHolder(ItemTempleCardHorizontalBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(Temple temple, OnTempleClickListener listener) {
            b.tvTempleName.setText(temple.getName());
            b.tvTempleCity.setText(temple.getLocation());
            b.tvDistanceBadge.setText(temple.getDistance());

            bindQueueStatus(temple.getQueueStatus(), temple.getQueueText());

            Glide.with(b.ivTempleImage.getContext())
                    .load(temple.getImage())
                    .placeholder(R.drawable.ic_temple_placeholder)
                    .error(R.drawable.ic_temple_placeholder)
                    .centerCrop()
                    .into(b.ivTempleImage);

            b.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onTempleClick(temple);
            });
            b.btnJoinQueue.setOnClickListener(v -> {
                if (listener != null) listener.onTempleClick(temple);
            });
        }

        private void bindQueueStatus(String status, String queueText) {
            // Live Queue chip — always green per spec
            String chipLabel = (queueText != null && !queueText.isEmpty())
                    ? "● " + queueText
                    : "● Live Queue";
            b.tvQueueStatus.setText(chipLabel);

            // Wait time derived from queue status
            String waitTime;
            int chipBg;
            int chipText;

            if ("HIGH".equalsIgnoreCase(status)) {
                waitTime = "~60+ min wait";
                chipBg = R.color.error_light;
                chipText = R.color.error;
            } else if ("MEDIUM".equalsIgnoreCase(status)) {
                waitTime = "~30 min wait";
                chipBg = R.color.warning_light;
                chipText = R.color.warning;
            } else {
                // Default: LOW / unknown → green
                waitTime = "~10 min wait";
                chipBg = R.color.success_light;
                chipText = R.color.success_foreground;
            }

            b.tvWaitTime.setText(waitTime);
            b.tvQueueStatus.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(b.getRoot().getContext(), chipBg)));
            b.tvQueueStatus.setTextColor(
                    ContextCompat.getColor(b.getRoot().getContext(), chipText));
        }
    }
}
