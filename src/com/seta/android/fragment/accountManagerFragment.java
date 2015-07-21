package com.seta.android.fragment;

import org.jivesoftware.smackx.packet.VCard;
import com.seta.android.recordchat.R;
import com.seta.android.xmppmanager.XmppConnection;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ling on 2015/4/29.
 */
public class accountManagerFragment extends Fragment{
    private TextView tv_first;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accountmanagerfragment, container, false);
        return view;
    }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//add by anshe 2015.7.8
		/*VCard vCard = new VCard();
		vCard.load(XmppConnection.reConnection());
		if("".equals(vCard.getNickName()) || null == vCard.getNickName()){
			System.out.println("昵称是空的");
			vCard.setNickName("快乐的汤姆猫");
		}*/
		this.setHasOptionsMenu(true);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_menu, menu);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
    
    
}

