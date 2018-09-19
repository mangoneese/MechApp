package com.mercury.mechanize;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;

public class WelcomeActivity extends AppCompatActivity {

    GridLayout mainGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mainGrid = findViewById(R.id.mainGrid);

        setSingleEvent(mainGrid);
        setToggleEvent(mainGrid);

    }

    private void setToggleEvent(GridLayout mainGrid){
        for(int i=0;i<mainGrid.getChildCount();i++)
        {
            final CardView cardView = (CardView)mainGrid.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cardView.getCardBackgroundColor().getDefaultColor() ==1)
                    {
                        cardView.setCardBackgroundColor(Color.parseColor("#FF6F00"));
                    }
                    else{
                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

                    }
                }
            });
        }
    }

    private void setSingleEvent(GridLayout mainGrid) {

        for(int i=0;i<mainGrid.getChildCount();i++)
        {
            CardView cardView = (CardView)mainGrid.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });
        }

    }
}
