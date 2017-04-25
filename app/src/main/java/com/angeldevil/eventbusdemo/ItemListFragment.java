package com.angeldevil.eventbusdemo;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.angeldevil.eventbusdemo.Event.ItemListEvent;

import de.greenrobot.event.EventBus;

public class ItemListFragment extends ListFragment
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Register
		EventBus.getDefault().register(this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// Unregister
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		// 开启线程加载列表
		new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(2000); // 模拟延时
					// 发布事件，在后台线程发的事件
					EventBus.getDefault().post(new ItemListEvent(Item.ITEMS));
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			};
		}.start();
	}
//虽然是灰色的,但是已经被调用了,当ItemListFragment中Item被点击时，发布了一个事件：EventBus.getDefault().post(getListView().getItemAtPosition(position));
// 实参的类型恰好是Item，于是触发我们的onEventMainThread方法，并把Item实参传递进来，我们更新控件。
/*
	onEventMainThread代表这个方法会在UI线程执行
	onEventPostThread代表这个方法会在当前发布事件的线程执行
	BackgroundThread这个方法，如果在非UI线程发布的事件，则直接执行，和发布在同一个线程中。如果在UI线程发布的事件，则加入后台任务队列，使用线程池一个接一个调用。
	Async 加入后台任务队列，使用线程池调用，注意没有BackgroundThread中的一个接一个。
*/

	public void onEventMainThread(ItemListEvent event)
	{
		setListAdapter(new ArrayAdapter<Item>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, event.getItems()));
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id)
	{
		super.onListItemClick(listView, view, position, id);
		//发布事件
		EventBus.getDefault().post(getListView().getItemAtPosition(position));
	}

}
