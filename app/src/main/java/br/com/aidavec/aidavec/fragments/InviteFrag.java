package br.com.aidavec.aidavec.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.adapters.InviteAdapter;
import br.com.aidavec.aidavec.adapters.SpinAdapter;
import br.com.aidavec.aidavec.models.Contacts;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class InviteFrag extends Fragment {

    public static List<Contacts> listObj;

    ListView lstView;
    InviteAdapter adapter;
    LayoutInflater inflater;
    Button btnSend;
    TextView lblAviso;
    String text = "Gostaria de te convidar para a Aidavec.\n" +
            "\n" +
            "www.aidavec.com.br\n" +
            "\n" +
            "UMA NOVA RENDA, A MESMA ROTINA.";

    public static Handler settingsHandler;
    Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
            refreshUpdates();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_invite, container, false);
        this.inflater = inflater;

//        listObj = new ArrayList<Contacts>();

//        lstView = (ListView) v.findViewById(R.id.lstView);
        lblAviso = (TextView) v.findViewById(R.id.lblAviso);
      //  lblAviso.setText("" + text);

        // Api.getInstance().GetNotes(handler);

/*        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NoteDetailsFrag.obj = listObj.get(i);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_contentframe, new NoteDetailsFrag(), "TAG").commit();
            }
        });
*/

        btnSend = (Button) v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

//        getContacts();

        return v;
    }

    public void refreshUpdates() {
        if (listObj != null && listObj.size() > 0) {
            adapter = new InviteAdapter(inflater, getContext());
            lstView.setAdapter(adapter);
        }
    }

    public void getContacts() {
        Cursor c = getActivity().getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[] { ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY },
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[] { "com.whatsapp" },
                null);

      //  ArrayList<String> myWhatsappContacts = new ArrayList<String>();
        int contactNameColumn = c.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY);
        int contactIdColumn = c.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID);
        while (c.moveToNext())
        {
            // You can also read RawContacts.CONTACT_ID to read the
            // ContactsContract.Contacts table or any of the other related ones.
            Contacts contact = new Contacts();
            contact.setName(c.getString(contactNameColumn));
            contact.setNumber(c.getString(contactIdColumn));
            listObj.add(contact);
        }

        refreshUpdates();
    }

    public void sendMessage() {


//        for (Contacts c : listObj) {
//            if (c.isSelected()) {
                Intent i = new Intent(Intent.ACTION_SEND); //, Uri.parse("content://com.android.contacts/data/" + c.getNumber()));

                i.setType("text/plain");
                i.setPackage("com.whatsapp");
                i.putExtra(Intent.EXTRA_SUBJECT, "Aidavec");
                i.putExtra(Intent.EXTRA_TEXT, text);

                if (i != null) {
                    startActivity(Intent.createChooser(i, text));
                } else {
                    Toast.makeText(getContext(), "É necessário instalar o WhatsApp.", Toast.LENGTH_SHORT)
                            .show();
                }
//            }
//        }

    }
}
