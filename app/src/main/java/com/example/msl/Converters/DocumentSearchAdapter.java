package com.example.msl.Converters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.msl.Instance.Document;

import java.util.ArrayList;
import java.util.List;

public class DocumentSearchAdapter extends ArrayAdapter<Document> implements Filterable {
    private List<Document> originalList;
    private List<Document> filteredList;
    private ItemFilter itemFilter;

    public DocumentSearchAdapter(Context context, List<Document> documents) {
        super(context, 0, documents);
        originalList = new ArrayList<>(documents);
        filteredList = new ArrayList<>(documents);
        itemFilter = new ItemFilter();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Document getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        Document document = getItem(position);
        if (document != null) {
            textView.setText(document.getTitle());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                String filterString = constraint.toString().toLowerCase();
                List<Document> filteredDocuments = new ArrayList<>();

                for (Document document : originalList) {
                    if (document.getBody().toLowerCase().contains(filterString)) {
                        filteredDocuments.add(document);
                    }
                }

                results.count = filteredDocuments.size();
                results.values = filteredDocuments;
            } else {
                results.count = originalList.size();
                results.values = originalList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (List<Document>) results.values;
            notifyDataSetChanged();
        }
    }
}
