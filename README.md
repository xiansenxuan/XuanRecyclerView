XuanRecyclerView
========
- 一个开源RecyclerView，支持添加head，伸缩head，下拉刷新，上拉加载，自定义空数据布局
- 支持GridLayoutManager LinearLayoutManager StaggeredGridLayoutManager
- 支持CoordinatorLayout SwipeRefreshLayout 伸缩及刷新

- An open source RecyclerView,enable adds head、telescopic head、drop down to refresh、pull loads and custom data layouts
- Enable GridLayoutManager LinearLayoutManager StaggeredGridLayoutManager
- Enable CoordinatorLayout SwipeRefreshLayout 

Add XuanRecyclerView to your project
========
If you want use this library, you only have to download XuanRecyclerView project, import it into your workspace and add the project as a library in your android project settings.

If you prefer it, you can use the gradle dependency, you have to add these lines in your build.gradle file:

```xml
repositories {
    jcenter()
}

dependencies {
    compile 'com.xuan.recyclerview:XuanRecyclerView:1.0'
}

<dependency>
  <groupId>com.xuan.recyclerview</groupId>
  <artifactId>XuanRecyclerView</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```

How to use
========
XML...
- Please consistent section ID
```xml
<android.support.v4.widget.SwipeRefreshLayout
    android:id="@+id/sw_layout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.xuan.recyclerview.XuanRecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_marginTop="0dp" />

        <include
            android:id="@+id/recycler_empty_view"
            layout="@layout/recycler_empty_view" />
    </FrameLayout>
</android.support.v4.widget.SwipeRefreshLayout>
```
CODE...
- Initialize XuanRecyclerView
```java
    RecyclerViewManager.initRecyclerView(context, recycler_view, sw_layout, recycler_empty_view, adapter,
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    queryData(ContactManager.onRefresh);
                }
            }, new XuanRecyclerView.OnLoadMoreListener() {
                @Override
                public void loadMore() {
                    queryData(ContactManager.onLoadMore);
                }
            },
            new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(getActivity(), "position=" + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    Toast.makeText(getActivity(), "position=" + position, Toast.LENGTH_SHORT).show();
                }
            });
```            
- Adapter
```java
class MyRecyclerViewAdapter extends XuanRecyclerViewAdapter<MyRecyclerViewAdapter.MyRecyclerViewHolder> {
    class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        View view;
        public MyRecyclerViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    @Override
    public MyRecyclerViewHolder getViewHolder(View view) {
        return new MyRecyclerViewHolder(view);
    }

    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        MyRecyclerViewHolder holder = new MyRecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindNormalViewHolder(MyRecyclerViewHolder holder, int position) {
        Object data = (Object) itemList.get(position);
    }

}
```
- Refresh And LoadMore 
```java
    queryData(ContactManager.onRefresh);
    queryData(ContactManager.onLoadMore);
    RecyclerViewManager.onRefresh(state,ArrayList,recycler_view,sw_layout,adapter);
```
- Details of reference demo

- thanks start and fork


Image
========
<img src="https://github.com/xiansenxuan/XuanRecyclerView/blob/master/images/recyclerview.gif" width = "320" height = "480" alt="sample"/>

Thanks
========
- [UltimateRecyclerView](https://github.com/cymcsg/UltimateRecyclerView)

```
Copyright 2015

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
