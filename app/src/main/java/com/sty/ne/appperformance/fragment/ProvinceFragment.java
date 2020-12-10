package com.sty.ne.appperformance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sty.ne.appperformance.R;
import com.sty.ne.appperformance.adapter.BaseAdapter;
import com.sty.ne.appperformance.adapter.BaseDelegate;
import com.sty.ne.appperformance.adapter.BaseViewHolder;
import com.sty.ne.appperformance.model.Province;
import com.sty.ne.appperformance.net.common.Result;
import com.sty.ne.appperformance.viewmodel.MiscViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 8:42 PM
 */
public class ProvinceFragment extends Fragment {
    private MiscViewModel viewModel;

    private RecyclerView recyclerView;

    private List<Province> list;

    protected BaseAdapter adapter;

    public static ProvinceFragment newInstance() {
        return new ProvinceFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_province, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MiscViewModel.class);
        findViews();
        initRv();
        loadData();
    }
    private void findViews() {
        recyclerView = getView().findViewById(R.id.recycler_view);
    }
    private void initRv() {
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseAdapter(list);
        adapter.setDelegate(new BaseDelegate<Province>() {

            @Override
            public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ProvinceViewHolder(parent);
            }

            @Override
            public int getItemViewType(Province data, int pos) {
                return 0;
            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void loadData() {
        viewModel.province().observe(getActivity(), new Observer<Result<List<Province>>>() {

            @Override
            public void onChanged(Result<List<Province>> result) {
                if (result.isSuccess() && result.getData() != null) {
                    list.addAll(result.getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private final class ProvinceViewHolder extends BaseViewHolder<Province> {

        /**
         * single view may be direct construction, eg: TextView view = new TextView(context);
         *
         * @param parent current no use, may be future use
         */
        public ProvinceViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_province_view);
        }

        private TextView province;

        @Override
        public void findViews() {
            province = itemView.findViewById(R.id.province);
        }

        @Override
        protected void onBindViewHolder(Province data) {
            province.setText(data.getProvince());
        }
    }
}
