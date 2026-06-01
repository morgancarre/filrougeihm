package com.example.filrouge_tp3;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


public class MenuFragment extends Fragment{
    private final String TAG = "frallo "+getClass().getSimpleName();
    private Menuable menuable;
    private int currentActivatedIndex = 0;
    private  View layout;


    public MenuFragment() {
        //Log.d(TAG, "MenuFragment created");
    }

    public void setCurrentActivatedIndex(int index){
        //here, currentActivatedIndex is not still updated --> currentActivatedIndex is the old one and index is the new selected
        Log.d(TAG,"setCurrentActivatedIndex updated to " + index +" (currentActivatedIndex = "+currentActivatedIndex+")");
        List<ImageView> imageViews = findPicturesMenuFromId( layout.findViewById(R.id.itemsMenu));
        imageViews.get(currentActivatedIndex).setImageResource(  layout.getResources().getIdentifier("menu"+(currentActivatedIndex), "mipmap", layout.getContext().getPackageName()) );
        imageViews.get(index).setImageResource(  layout.getResources().getIdentifier("menu"+(index)+"_s", "mipmap", layout.getContext().getPackageName()) );
        currentActivatedIndex = index;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_menu, container, false);

        List<ImageView> imageViews = findPicturesMenuFromId( layout.findViewById(R.id.itemsMenu));

        //get current activated index menu
        if (getArguments() != null) {
            currentActivatedIndex = getArguments().getInt(getString(R.string.index), 0);  //convert menu number to index
        }
        imageViews.get(currentActivatedIndex).setImageResource(  layout.getResources().getIdentifier("menu"+(currentActivatedIndex)+"_s", "mipmap", layout.getContext().getPackageName()) );
        //Log.d(TAG, "BEGIN : menu index " +  currentActivatedIndex + " is selected");

        //notify activity the menu is selected
        menuable.onMenuChange(currentActivatedIndex);

        TextView text = layout.findViewById(R.id.txtFragmentMenu);
        text.setText("Menu");
       // menuable.onMenuChange(currentActivatedIndex);


        for(ImageView imageView : imageViews) {
            imageView.setOnClickListener( menu -> {
                int oldIndex = currentActivatedIndex;
                currentActivatedIndex = Integer.parseInt(imageView.getTag().toString());

                //notify activity currentIndexChange
                menuable.onMenuChange(currentActivatedIndex);

                //display old menu in gray
                imageViews.get(oldIndex).setImageResource(  layout.getResources().getIdentifier("menu"+(oldIndex), "mipmap", layout.getContext().getPackageName()) );

                //display new menu in green
                ((ImageView)menu).setImageResource(  layout.getResources().getIdentifier("menu"+(currentActivatedIndex)+"_s", "mipmap", layout.getContext().getPackageName()) );
            });
        }
        return layout;
    }





    // browse rootView and sort all buttons in "buttons" list
    private List<ImageView> findPicturesMenuFromId(View view) {
        List<ImageView> pictures = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof ImageView) {
                    String idString = getResources().getResourceEntryName(child.getId());
                    if (idString.matches("menu[1-9]?")) {
                        pictures.add((ImageView) child);
                    }
                }
            }
        }
        return pictures;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (requireActivity() instanceof Menuable) {
            menuable = (Menuable) requireActivity();
            //Log.d(TAG, "Class " + requireActivity().getClass().getSimpleName() + " implements Notifiable.");
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

}
