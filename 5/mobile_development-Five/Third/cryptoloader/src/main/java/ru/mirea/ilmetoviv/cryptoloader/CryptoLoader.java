package ru.mirea.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class CryptoLoader extends AsyncTaskLoader<String> {

    public static final String ARG_CIPHER_TEXT = "cipher_text";
    public static final String ARG_KEY = "key";
    private static final String TAG = "CryptoLoader";

    private final byte[] cipherText;
    private final byte[] keyBytes;

    public CryptoLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.cipherText = args.getByteArray(ARG_CIPHER_TEXT);
        this.keyBytes = args.getByteArray(ARG_KEY);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(TAG, "onStartLoading: начинаем дешифровку");
        forceLoad(); // ← Обязательно! Иначе loadInBackground не вызовется
    }

    @Override
    public String loadInBackground() {
        Log.d(TAG, "loadInBackground: поток = " + Thread.currentThread().getName());

        // Имитация «тяжёлой» операции (по заданию — задержка)
        SystemClock.sleep(2000);

        try {
            // Восстанавливаем ключ из байтов
            SecretKey originalKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");

            // Дешифруем
            return decryptMsg(cipherText, originalKey);

        } catch (Exception e) {
            Log.e(TAG, "Ошибка дешифровки", e);
            return "Ошибка: " + e.getMessage();
        }
    }

    // ===== Крипто-методы (из задания) =====

    public static SecretKey generateKey() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes()); // ⚠️ Фиксированный seed — только для обучения!
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256, sr);
            return new SecretKeySpec(kg.generateKey().getEncoded(), "AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptMsg(String message, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return cipher.doFinal(message.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}