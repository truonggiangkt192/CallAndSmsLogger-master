package com.call.logger.app.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.call.logger.app.main.adapters.SmsAdapter;
import com.call.logger.app.main.backup.GetSelectedItemsInfo;
import com.call.logger.app.main.backup.email.SendEmail;
import com.call.logger.app.main.backup.file.SaveToFile;
import com.call.logger.app.main.database.CallAndSmsSQLiteOpenHelper;
import com.call.logger.app.main.model.Sms;
import com.call.logger.app.main.settings.SettingsFragment;
import com.call.logger.app.main.sync.SyncCallsAndSms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmsFragment extends Fragment {
    private List<Sms> smsList;
    private SendEmail sendEmail;
    private ArrayAdapter adapter;
    private GetSelectedItemsInfo itemsInfo;
    private CallAndSmsSQLiteOpenHelper openHelper;
    private ProgressBar bar;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sms_fragment, container, false);
        SyncCallsAndSms.syncSms(getActivity());
        setHasOptionsMenu(true);
        sendEmail = new SendEmail(getActivity());
        itemsInfo = new GetSelectedItemsInfo(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetSmsTask().execute();
    }

    private class GetSmsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(0);
            bar.setMax(100);
        }

        @Override
        protected Void doInBackground(Void... params) {
            smsList = new ArrayList<Sms>();
            openHelper = new CallAndSmsSQLiteOpenHelper(getActivity());
            smsList = openHelper.getAllSms();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Collections.reverse(smsList);
            ListView listView = (ListView) getActivity().findViewById(R.id.listViewSms);
            adapter = new SmsAdapter(getActivity(), smsList);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Sms sms = smsList.get(position);
                    if (sms.isSelected()) {
                        sms.setSelected(false);
                        adapter.notifyDataSetChanged();
                    } else {
                        sms.setSelected(true);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            bar.setProgress(0);
            bar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            bar.incrementProgressBy(10);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.contextSmsDelete:
                delete(smsList.get(info.position).getId());
                return true;
            default: return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_sms, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSend:
                menuSend();
                return true;
            case R.id.menuSelect:
                selectAllItems(smsList);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.menuDeselect:
                deselectAllItems(smsList);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.menuSave:
                menuSave();
                return true;
            case R.id.menuDelete:
                menuDelete();
                return true;
            case R.id.menuSettings:
                Intent settings = new Intent(getActivity(), SettingsFragment.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void menuSend() {
        if (sendEmail.isNetworkConnected()) {
            if (itemsInfo.isSelectedSms(smsList)) {
                sendEmail.sendSms(smsList);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_select), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void menuSave() {
        if (itemsInfo.isSelectedSms(smsList)) {
            new SaveToFile(getActivity()).save(itemsInfo.getSmsInfo(smsList));
            Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_select), Toast.LENGTH_SHORT).show();
        }
    }

    private void menuDelete() {
        if (itemsInfo.isSelectedSms(smsList)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.alert_title_sms));
            builder.setMessage(getString(R.string.alert_msg));
            builder.setPositiveButton(getString(R.string.btn_pos), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    for (Sms sms : smsList) {
                        if (sms.isSelected()) {
                            openHelper.deleteSms(sms.getId());
                        }
                    }
                    new GetSmsTask().execute();
                }
            });
            builder.setNegativeButton(getString(R.string.btn_neg), null).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_select), Toast.LENGTH_SHORT).show();
        }
    }

    private void delete(final int smsId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.alert_title_sms));
        builder.setMessage(getString(R.string.alert_msg));
        builder.setPositiveButton(getString(R.string.btn_pos), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openHelper.deleteSms(smsId);
                new GetSmsTask().execute();
            }
        });
        builder.setNegativeButton(getString(R.string.btn_neg), null).show();
    }

    private void selectAllItems(List<Sms> smsList) {
        for (Sms sms : smsList) {
            sms.setSelected(true);
        }
    }

    private void deselectAllItems(List<Sms> smsList) {
        for (Sms sms : smsList) {
            sms.setSelected(false);
        }
    }
}