package ru.mirea.ilmetoviv.mireaproject.ui.data; // ПРОВЕРЬ ПАКЕТ!

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ru.mirea.ilmetoviv.mireaproject.databinding.FragmentDataBinding;

public class DataFragment extends Fragment {
    private FragmentDataBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Инициализация Binding
        binding = FragmentDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Очистка памяти
    }
}