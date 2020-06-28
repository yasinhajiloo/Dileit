package com.example.dileit.view.fragment.leitnermanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.dileit.constant.LeitnerStateConstant;
import com.example.dileit.databinding.FragmentReviewWordsManagerBinding;
import com.example.dileit.model.entity.Leitner;
import com.example.dileit.view.adapter.recycler.LeitnerManagerRecyclerAdapter;
import com.example.dileit.viewmodel.InternalViewModel;

import java.util.List;

public class ReviewWordsManagerFragment extends Fragment implements LeitnerManagerRecyclerAdapter.LeitnerManagerInterface {

    private final String TAG = ReviewWordsManagerFragment.class.getSimpleName();
    private FragmentReviewWordsManagerBinding mBinding;
    private InternalViewModel mInternalViewModel;
    private LeitnerManagerRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInternalViewModel = ViewModelProviders.of(this).get(InternalViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentReviewWordsManagerBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new LeitnerManagerRecyclerAdapter(this);

        mBinding.rvReviewManager.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mBinding.rvReviewManager.setAdapter(adapter);


        //showing box1 items in startup
        mInternalViewModel.setBoxState(LeitnerStateConstant.BOX_ONE);

        mInternalViewModel.getLearnedCardByBox().observe(getViewLifecycleOwner(), leitners -> {
            setData(leitners);
            for (Leitner leitner:
                 leitners) {
                Log.d(TAG, leitner.toString());
            }
        });

        mBinding.chipFilteredBoxOne.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mInternalViewModel.setBoxState(LeitnerStateConstant.BOX_ONE);
            }
        });

        mBinding.chipFilteredBoxTwo.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mInternalViewModel.setBoxState(LeitnerStateConstant.BOX_TWO);
            }
        });


        mBinding.chipFilteredBoxThree.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mInternalViewModel.setBoxState(LeitnerStateConstant.BOX_THREE);
            }
        });


        mBinding.chipFilteredBoxFour.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mInternalViewModel.setBoxState(LeitnerStateConstant.BOX_FOUR);
            }
        });

        mBinding.chipFilteredBoxFive.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mInternalViewModel.setBoxState(LeitnerStateConstant.BOX_FIVE);
            }
        });


        mInternalViewModel.getDeletedItemStatus().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer > 0) {
                    Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setData(List<Leitner> leitnerList) {
        if (leitnerList.size() > 0) {
            mBinding.rvReviewManager.setVisibility(View.GONE);
            mBinding.rvReviewManager.setVisibility(View.VISIBLE);
            mBinding.tvNoDataReviewMan.setVisibility(View.GONE);
            adapter.setData(leitnerList);
        } else {
            mBinding.rvReviewManager.setVisibility(View.GONE);
            mBinding.tvNoDataReviewMan.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDeleteSelected(Leitner leitner) {
        mInternalViewModel.deleteLeitnerItem(leitner);
        Toast.makeText(getContext(), "" + adapter.getItemCount(), Toast.LENGTH_SHORT).show();
        if (adapter.getItemCount() == 0) {
            mBinding.rvReviewManager.setVisibility(View.GONE);
            mBinding.tvNoDataReviewMan.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditSelected(Leitner leitner) {
        LeitnerManagerHandler.edit(this, leitner);
    }
}
