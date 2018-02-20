package br.com.aidavec.aidavec.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import br.com.aidavec.aidavec.R;

/**
 * Created by leonardo.saganski on 03/01/17.
 */

public class AboutFrag extends Fragment {

    Button btnFaq;
    Button btnTermos;
    Intent browserIntent;
    TextView txtWebsite;
    TextView txtMailTo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_about, container, false);

        btnFaq = (Button) v.findViewById(R.id.btnFaq);
        btnTermos = (Button) v.findViewById(R.id.btnTermos);
        txtWebsite = (TextView) v.findViewById(R.id.txtWebsite);
        txtMailTo = (TextView) v.findViewById(R.id.txtMailTo);

        btnFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.aidavec.com.br/faq"));
                startActivity(browserIntent);
            }
        });

        btnTermos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.aidavec.com.br/termos"));
                startActivity(browserIntent);
            }
        });

        txtWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.aidavec.com.br"));
                startActivity(browserIntent);
            }
        });

        txtMailTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserIntent = new Intent(Intent.ACTION_SEND);
                browserIntent.setType("text/plain");
                browserIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"suporte@aidavec.com.br"});
                startActivity(Intent.createChooser(browserIntent, "Enviar e-mail"));
            }
        });

        return v;
    }

}