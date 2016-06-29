package com.call.logger.app.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.call.logger.app.main.adapters.CallAdapter;
import com.call.logger.app.main.backup.GetSelectedItemsInfo;
import com.call.logger.app.main.backup.email.SendEmail;
import com.call.logger.app.main.backup.file.SaveToFile;
import com.call.logger.app.main.database.CallAndSmsSQLiteOpenHelper;
import com.call.logger.app.main.model.Call;
import com.call.logger.app.main.settings.SettingsFragment;
import com.call.logger.app.main.sync.SyncCallsAndSms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CallFragment extends Fragment {
    private List<Call> callList;
    private SendEmail sendEmail;
    private ArrayAdapter<Call> adapter;
    private CallAndSmsSQLiteOpenHelper openHelper;
    private GetSelectedItemsInfo itemsInfo;
    private ProgressBar bar;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.call_fragment, container, false);
        SyncCallsAndSms.syncCalls(getActivity());
        setHasOptionsMenu(true);
        sendEmail = new SendEmail(getActivity());
        itemsInfo = new GetSelectedItemsInfo(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetCallsTask().execute();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.contextDelete:
                delete(callList.get(info.position).getId());
                return true;
            case R.id.contextCall:
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + callList.get(info.position).getNumber()));
                startActivity(call);
            default: return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_call, menu);
    }

    private class GetCallsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(0);
            bar.setMax(100);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            callList = new ArrayList<Call>();
            openHelper = new CallAndSmsSQLiteOpenHelper(getActivity());
            callList = openHelper.getAllCalls();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ListView listView = (ListView) getView().findViewById(R.id.listViewCall);
            Collections.reverse(callList);
            adapter = new CallAdapter(getActivity(), callList);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Call call = callList.get(position);
                    if (call.isSelected()) {
                        call.setSelected(false);
                        adapter.notifyDataSetChanged();
                    } else {
                        call.setSelected(true);
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
                selectAllItems(callList);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.menuDeselect:
                deselectAllItems(callList);
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

    private void selectAllItems(List<Call> calls) {
        for (Call call : calls) {
            call.setSelected(true);
        }
    }

    private void deselectAllItems(List<Call> calls) {
        for (Call call : calls) {
            call.setSelected(false);
        }
    }

    private void menuSend() {
        if (sendEmail.isNetworkConnected()) {
            if (itemsInfo.isSelectedCalls(callList)) {
                sendEmail.sendCalls(callList);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_select), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void menuSave() {
        if (itemsInfo.isSelectedCalls(callList)) {
            new SaveToFile(getActivity()).save(itemsInfo.getCallsInfo(callList));
            Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_select), Toast.LENGTH_SHORT).show();
        }
    }

    private void menuDelete() {
        if (itemsInfo.isSelectedCalls(callList)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.alert_title));
            builder.setMessage(getString(R.string.alert_msg));
            builder.setPositiveButton(getString(R.string.btn_pos), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    for (Call call : callList) {
                        if (call.isSelected()) {
                            openHelper.deleteCall(call.getId());
                        }
                    }
                    new GetCallsTask().execute();
                }
            });
            builder.setNegativeButton(getString(R.string.btn_neg), null).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_select), Toast.LENGTH_SHORT).show();
        }
    }

    private void delete(final int callId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.alert_title2));
        builder.setMessage(getString(R.string.alert_msg));
        builder.setPositiveButton(getString(R.string.btn_pos), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openHelper.deleteCall(callId);
                new GetCallsTask().execute();
            }
        });
        builder.setNegativeButton(getString(R.string.btn_neg), null).show();
    }
}
