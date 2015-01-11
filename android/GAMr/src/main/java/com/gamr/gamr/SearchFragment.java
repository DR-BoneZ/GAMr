package com.gamr.gamr;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by beni on 1/10/15.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private CheckBox searchByGame, searchByGenre, searchByUsername;
    private CheckBox[] checkBoxes;
    private int checkedBox;
    private Button searchButton;
    int seriousness;
    private TextView seriousnessLabel;
    private SeekBar seriousnessSeekBar;
    private RadioGroup PLTorTLPgroup;
    private Boolean plt;
    private String searchType;


    @Override
    public void onCreate (Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, null);

        Spinner spinner = (Spinner) view.findViewById(R.id.search_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchButton = (Button) view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClicked();
            }
        });

        seriousness = 1;
        seriousnessLabel = (TextView) view.findViewById(R.id.search_seriousness_label);
        seriousnessSeekBar = (SeekBar) view.findViewById(R.id.search_seriousness);
        seriousnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                seriousness = Math.round(progress / 25) + 1;
                seriousnessLabel.setText(""+seriousness);
            }
        });

        plt = true;
        PLTorTLPgroup = (RadioGroup) view.findViewById(R.id.search_plt_or_tlp);
        PLTorTLPgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                plt = 0==i;
            }
        });

        return view;
    }

    private void onSearchClicked() {}

    @Override
    public void onPause () { super.onPause(); }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        searchType = (String) parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}
