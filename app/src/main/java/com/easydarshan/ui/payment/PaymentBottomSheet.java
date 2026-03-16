package com.easydarshan.ui.payment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easydarshan.R;
import com.easydarshan.data.model.UpiApp;
import com.easydarshan.ui.adapter.UpiAppAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class PaymentBottomSheet extends BottomSheetDialogFragment {

    private String amount;
    private String merchantName = "Harish D N";
    private String merchantUpiId = "9618004438@ybl"; 
    private UpiApp selectedUpiApp;
    private List<UpiApp> allUpiApps = new ArrayList<>();
    private boolean isShowingAllUpi = false;
    private UpiAppAdapter upiAdapter;

    private static final int UPI_INTENT_REQUEST_CODE = 401;

    public static PaymentBottomSheet newInstance(String amount) {
        PaymentBottomSheet fragment = new PaymentBottomSheet();
        Bundle args = new Bundle();
        args.putString("amount", amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            amount = getArguments().getString("amount");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.tvMerchantName)).setText(merchantName);
        ((TextView) view.findViewById(R.id.tvAmountTop)).setText("₹ " + amount);
        ((TextView) view.findViewById(R.id.tvAmountSticky)).setText("₹ " + amount);

        allUpiApps = getInstalledUpiApps();
        
        RecyclerView rvUpiApps = view.findViewById(R.id.rvUpiApps);
        rvUpiApps.setLayoutManager(new GridLayoutManager(getContext(), 3));
        
        updateUpiList(view);

        view.findViewById(R.id.btnShowMoreUpi).setOnClickListener(v -> {
            isShowingAllUpi = !isShowingAllUpi;
            updateUpiList(view);
        });

        // Other Methods - returning success directly as requested (no Razorpay)
        View.OnClickListener successSimulated = v -> handleFinalSuccess();

        view.findViewById(R.id.btnCards).setOnClickListener(successSimulated);
        view.findViewById(R.id.btnNetbanking).setOnClickListener(successSimulated);
        view.findViewById(R.id.btnWallet).setOnClickListener(successSimulated);
        view.findViewById(R.id.btnPayLater).setOnClickListener(successSimulated);
        
        view.findViewById(R.id.btnContinuePayment).setEnabled(true);
        view.findViewById(R.id.btnContinuePayment).setOnClickListener(v -> {
            if (selectedUpiApp != null) {
                launchDirectUpiIntent(selectedUpiApp);
            } else {
                handleFinalSuccess();
            }
        });
    }

    private void updateUpiList(View view) {
        List<UpiApp> displayList;
        TextView btnShowMore = view.findViewById(R.id.btnShowMoreUpi);

        if (isShowingAllUpi || allUpiApps.size() <= 3) {
            displayList = allUpiApps;
            btnShowMore.setText(allUpiApps.size() <= 3 ? "" : "Show Less ▴");
        } else {
            displayList = allUpiApps.subList(0, 3);
            btnShowMore.setText("Show More UPI Apps ▾");
        }

        upiAdapter = new UpiAppAdapter(displayList, app -> {
            selectedUpiApp = app;
            updateSelectionUI();
            // Automatically launch if an app is selected
            launchDirectUpiIntent(app);
        });
        
        RecyclerView rvUpiApps = view.findViewById(R.id.rvUpiApps);
        rvUpiApps.setAdapter(upiAdapter);
    }

    private void updateSelectionUI() {
        TextView tvSelectedMethod = requireView().findViewById(R.id.tvSelectedMethod);
        if (selectedUpiApp != null) {
            tvSelectedMethod.setText("Selected: " + selectedUpiApp.getName());
        }
    }

    private void launchDirectUpiIntent(UpiApp app) {
        // Simplified UPI intent for personal VPA to avoid security warnings
        // Standard P2P format: upi://pay?pa=VPA&pn=NAME&am=AMOUNT&cu=INR&tn=NOTE
        Uri uri = new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", merchantUpiId)
                .appendQueryParameter("pn", merchantName)
                .appendQueryParameter("tn", "Darshan Booking")
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(app.getPackageName());

        try {
            startActivityForResult(intent, UPI_INTENT_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Could not open " + app.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPI_INTENT_REQUEST_CODE) {
            // After returning from UPI app, we return success to the flow as requested
            handleFinalSuccess();
        }
    }

    private void handleFinalSuccess() {
        if (getActivity() instanceof PaymentCallback) {
            ((PaymentCallback) getActivity()).onPaymentSuccess();
        }
        dismiss();
    }

    private List<UpiApp> getInstalledUpiApps() {
        List<UpiApp> upiApps = new ArrayList<>();
        PackageManager pm = requireContext().getPackageManager();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("upi://pay"));

        List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        
        for (ResolveInfo info : activities) {
            String name = info.loadLabel(pm).toString();
            Drawable icon = info.loadIcon(pm);
            upiApps.add(new UpiApp(name, info.activityInfo.packageName, icon));
        }
        return upiApps;
    }

    public interface PaymentCallback {
        void onPaymentSuccess();
    }
}
