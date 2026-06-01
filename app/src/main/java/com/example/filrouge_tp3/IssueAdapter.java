package com.example.filrouge_tp3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class IssueAdapter extends ArrayAdapter<Issue> {

    private final ClickableIssue<Issue> listener;
    private final List<Issue> issues;

    public IssueAdapter(Context context, List<Issue> issues, ClickableIssue<Issue> listener) {
        super(context, R.layout.item_issue, issues);
        this.issues = issues;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_issue, parent, false);
            holder = new ViewHolder();
            holder.title       = convertView.findViewById(R.id.itemTitle);
            holder.description = convertView.findViewById(R.id.itemDescription);
            holder.priority    = convertView.findViewById(R.id.itemPriority);
            holder.ratingBar   = convertView.findViewById(R.id.itemRatingBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Issue issue = issues.get(position);

        if (issue != null) {
            issue.addObserver(EmergencyService.getInstance());

            holder.title.setText(issue.getTitle());
            holder.description.setText(issue.getDescription());

            // Image selon la priorité
            switch (issue.getPriority()) {
                case CRITICAL: holder.priority.setImageResource(R.drawable.ic_critical); break;
                case HIGH:     holder.priority.setImageResource(R.drawable.ic_high);     break;
                case MEDIUM:   holder.priority.setImageResource(R.drawable.ic_medium);   break;
                case LOW:      holder.priority.setImageResource(R.drawable.ic_low);      break;
            }

            // RatingBar : on retire le listener avant de setValue pour éviter les callbacks parasites
            holder.ratingBar.setOnRatingBarChangeListener(null);
            holder.ratingBar.setRating(issue.getStatus());

            // Fix : permet à la RatingBar de recevoir les touches dans une ListView
            holder.ratingBar.setFocusable(false);
            holder.ratingBar.setClickable(true);
            holder.ratingBar.setIsIndicator(false);

            holder.ratingBar.setOnRatingBarChangeListener((rb, value, fromUser) -> {
                if (fromUser) {
                    issue.setStatus(value);
                    if (listener != null) {
                        listener.onRatingBarChange(position, value, this, issues);
                    }
                }
            });
        }

        // Clic sur toute la ligne UNIQUEMENT si le touch n'est pas sur la RatingBar
        convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClickItem(issues, position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView description;
        ImageView priority;
        RatingBar ratingBar;
    }
}