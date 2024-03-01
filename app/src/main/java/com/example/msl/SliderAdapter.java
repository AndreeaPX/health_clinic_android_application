package com.example.msl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.viewpager.widget.PagerAdapter;

import org.w3c.dom.Text;

public class SliderAdapter extends PagerAdapter {

    Context context; //activitate
    LayoutInflater layoutInflater;

    int slider_images[] = {
            R.drawable.story_1_certificate,
            R.drawable.story_2_checkup,
            R.drawable.story_3_report
    };

    int slider_headings[] = {
            R.string.story_1_welcome,
            R.string.story_2_welcome,
            R.string.story_3_welcome
    };

    int slider_descriptions[] = {
            R.string.story_1_description,
            R.string.story_2_description,
            R.string.story_3_description
    };

    //constructor
    public SliderAdapter(Context context) {
        this.context = context;
    }

    //how many slides are you using in the project
    @Override
    public int getCount() {
        return slider_descriptions.length; //it's better then using the exact number of slides, because ypu may change it in the future
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ScrollView) object; //if the layout exists or not
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        //request system to create a new layout
        View view = layoutInflater.inflate(R.layout.stories_layout,container, false);
        ImageView imageView = view.findViewById(R.id.slider_image);
        TextView heading = view.findViewById(R.id.slider_heading);
        TextView description = view.findViewById(R.id.slider_description);
        imageView.setImageResource(slider_images[position]);
        heading.setText(slider_headings[position]);
        description.setText(slider_descriptions[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((  ScrollView) object);
    }
}
