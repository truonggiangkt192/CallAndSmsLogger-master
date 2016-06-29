package tranduythanh.com;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {

	EditText editName,editPhone;
	Button btnSave;
	ListView lvContact;
	//Danh sách contact để đưa vào ListView
	ArrayList<MyContact>arrContact=new ArrayList<MyContact>();
	ArrayAdapter<MyContact>adapter=null;
	MyContact selectedContact=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		doGetFormWidgets();
		doAddEvents();
	}
	public void doGetFormWidgets()
	{
		btnSave=(Button) findViewById(R.id.btnSaveContact);
		editName=(EditText) findViewById(R.id.editName);
		editPhone=(EditText) findViewById(R.id.editPhone);
		lvContact=(ListView) findViewById(R.id.lvContact);
		//tạo đối tượng adapter
		adapter=new ArrayAdapter<MyContact>
		(this, android.R.layout.simple_list_item_1,arrContact);
		//gán Adapter vào cho ListView
		lvContact.setAdapter(adapter);
		//thiết lập contextmenu cho ListView
		registerForContextMenu(lvContact);
	}
	public void doAddEvents()
	{
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				doSaveContact();
			}
		});
		//lấy contact được chọn trước đó trong ListView
		//Vì khi mở context menu sẽ làm mất focus nên ta phải lưu lại trước
		//khi mở context menu
		lvContact.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//lưu vết contact được chọn trong ListView
				selectedContact=arrContact.get(arg2);
				return false;
			}
		});
	}
	/**
	 * mỗi lần nhấn Save contact thì gọi hàm này
	 * để cập nhạt contact vào List view
	 * bạn lưu ý là ta chỉ làm trong bộ nhớ
	 * không phải lưu vào Danh Bạ (phần này học sau)
	 */
	public void doSaveContact()
	{
		MyContact ct=new MyContact();
		ct.setName(editName.getText()+"");
		ct.setPhone(editPhone.getText()+"");
		arrContact.add(ct);
		adapter.notifyDataSetChanged();
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		//gắn context menu vào
		getMenuInflater().inflate(R.menu.phonecontextmenu, menu);
		menu.setHeaderTitle("Call - Sms");
		menu.getItem(0).setTitle("Call to "+selectedContact.getPhone());
		menu.getItem(1).setTitle("Send sms to "+selectedContact.getPhone());
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//kiểm tra xem Menu Item nào được chọn
		switch(item.getItemId())
		{
		case R.id.mnuCall:
			doMakeCall();
			break;
		case R.id.mnuSms:
			doMakeSms();
			break;
		case R.id.mnuRemove:
			arrContact.remove(selectedContact);
			adapter.notifyDataSetChanged();
			break;
		}
		return super.onContextItemSelected(item);
	}
	/**
	 * Thực hiện gọi điện thoại
	 */
	public void doMakeCall()
	{
		Uri uri=Uri.parse("tel:"+selectedContact.getPhone());
		Intent i=new Intent(Intent.ACTION_CALL, uri);
		startActivity(i);
	}
	/**
	 * thực hiện mở giao diện gửi tin nhắn
	 * Truyền thông tin contact đang chọn qua
	 * activity mới
	 */
	public void doMakeSms()
	{
		Intent i=new Intent(this, MySMSActivity.class);
		Bundle b=new Bundle();
		b.putSerializable("CONTACT", selectedContact);
		i.putExtra("DATA", b);
		startActivity(i);
	}
}
