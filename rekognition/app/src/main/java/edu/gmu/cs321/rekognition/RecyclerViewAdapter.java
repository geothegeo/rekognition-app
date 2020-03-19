package edu.gmu.cs321.rekognition;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the scroll bar for keywords
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<String> checkboxText;
    private List<String> keywords;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<String> keywordsList) {
        checkboxText = keywordsList;
        keywords = new ArrayList<>(keywordsList);
        mContext = context;
    }

    public List<String> getKeywords()
    {
        return keywords;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_keywords, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        final String word = checkboxText.get(position);

        holder.box.setText(word);


        holder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!(keywords.contains(word))) keywords.add(word);
                }
                else {
                    keywords.remove(word);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return checkboxText.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox box;

        public ViewHolder(View v) {
            super(v);
            box = (CheckBox) v.findViewById(R.id.cBox);
        }
    }
}
