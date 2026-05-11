package ru.mirea.goncharovas.mireaproject.ui.webview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ru.mirea.goncharovas.mireaproject.databinding.FragmentWebViewBinding;

public class WebViewFragment extends Fragment {
    private FragmentWebViewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWebViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        WebView webView = binding.webView;
        ProgressBar progressBar = binding.progressBar;

        // Настройки WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Разрешить JS
        webView.setWebViewClient(new WebViewClient()); // Открывать внутри приложения

        // Индикатор загрузки
        webView.setWebChromeClient(new android.webkit.WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
            }
        });

        webView.loadUrl("https://developer.android.com");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}