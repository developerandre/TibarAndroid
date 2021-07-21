package developer.own.tibar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.DrawableRes;

import com.google.zxing.BarcodeFormat;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

import vpos.apipackage.PosApiHelper;
import vpos.apipackage.Print;
import vpos.apipackage.PrintInitException;

public class PrinterModule {

    private static String tag = PrinterModule.class.getSimpleName();
    private SynchronizedPrinter printer = new SynchronizedPrinter();

    public synchronized int PrintCheckStatus() {
        return printer.PrintCheckStatus();
    }
    public synchronized void printInit() { doInBackground(() -> printer.printInit()); }
    public synchronized int sysSetPower(int mode) {
        return printer.sysSetPower(mode);
    }
    public synchronized void printSetGray(int level) { doInBackground(() -> printer.printSetGray(level)); }
    public synchronized void printSetFontSize(int size, int zoom) { doInBackground(() -> printer.printSetFontSize(size, zoom)); }
    public synchronized void printSetDefaultFont() { doInBackground(() -> printer.printSetDefaultFont()); }
    public synchronized void printSetAlign(int alignment) { doInBackground(() -> printer.printSetAlign(alignment)); }
    public synchronized void printString(String text) { doInBackground(() -> printer.printString(text)); }
    public synchronized void printBarcode(String content, int width, int height, BarcodeFormat barcodeFormat) {
        doInBackground(() -> printer.printBarcode(content, width, height, barcodeFormat));
    }
    public synchronized void printQrCode_Cut(String content, int width, int height) {
        doInBackground(() -> printer.printQrCode_Cut(content, width, height));
    }
    public synchronized void printCutQrCode_Str(String qrContent, String printTxt, int distance, int width, int height) {
        doInBackground(() -> printer.printCutQrCode_Str(qrContent, printTxt, distance, width, height));
    }
    public synchronized void printBitmap(byte[] bytes) {
        doInBackground(() -> printer.printBitmap(bytes));
    }
    public synchronized void printBitmapResource(Context context, @DrawableRes int drawableId) {
        doInBackground(() -> printer.printBitmapResource(context, drawableId));
    }
    public synchronized void printEndLine() { doInBackground(() -> printer.printEndLine()); }
    public synchronized void printStart(/*Callback<Integer> callback*/) {
        doInBackground(() -> printer.printStart(/*callback*/));
    }

    private void doInBackground(Runnable runnable) {
      Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static class SynchronizedPrinter {
        PosApiHelper posApiHelper = PosApiHelper.getInstance();

        public synchronized void printInit() {
            try {
                posApiHelper.PrintInit();
            } catch (PrintInitException e) {
                e.printStackTrace();
                int initRet = e.getExceptionCode();
                Log.e(tag, "initRer : " + initRet);
            }
        }
        
        public synchronized int PrintCheckStatus() {
            try {
                int i = posApiHelper.PrintCheckStatus();
                Log.e("PrintCheckStatus", "result: " + i);
                return i;
            } catch (Exception e) {
                Log.e("PrintCheckStatus", "error: " + e);
                return -1;
            }
        } 
        //Sys.Lib_Setpower(1);
        public synchronized int sysSetPower(int mode) {
            Log.e("sysSetPower", "mode: " + mode);
            int i = posApiHelper.SysSetPower(mode);
            Log.e("sysSetPower", "result: " + i);
            return i;
        }
        
        /**
         * Print density. Normal is 2
         * @param level ex : between 1 and 5
         */
        public synchronized void printSetGray(int level) {
            posApiHelper.PrintSetGray(level);
        }

        /**
         *
         * @param size default is 24
         */
        public synchronized void printSetFontSize(int size, int zoom) {
            posApiHelper.PrintSetFont((byte) size, (byte) size, (byte) zoom);
        }

        public synchronized void printSetDefaultFont() {
            posApiHelper.PrintSetFont((byte) 24, (byte) 24, (byte) 0x00);
        }

        /**
         * Useful for QR_code
         * @param alignment 0 Left ,1 Middle ,2 Right
         */
        public synchronized void printSetAlign(int alignment) {
            Print.Lib_PrnSetAlign(alignment);
        }

        public synchronized void printString(String text) {
            posApiHelper.PrintStr(text);
        }

        /**
         * Test {@link BarcodeFormat#CODE_128} with 360x120.
         * And also test {@link BarcodeFormat#QR_CODE} with 240*240
         * @param content ex : "1234567890"
         * @param width ex : 360
         * @param height ex : 360
         * @param barcodeFormat ex : {@link BarcodeFormat#QR_CODE}
         */
        public synchronized void printBarcode(String content, int width, int height,
                                              BarcodeFormat barcodeFormat) {
            posApiHelper.PrintBarcode(content, width, height, barcodeFormat);
        }

        /**
         *
         * @param content ex : "123456789"
         * @param width ex : 360
         * @param height ex : 360
         */
        public synchronized void printQrCode_Cut(String content, int width, int height) {
            //posApiHelper.PrintQrCode_Cut(content, width, height, BarcodeFormat.QR_CODE);
        }

        /**
         *
         * @param qrContent ex : "1234567890"
         * @param printTxt ex : "PK TXT adsad adasd sda"
         * @param distance ex : 5
         * @param width ex : 300
         * @param height ex : 300
         */
        public synchronized void printCutQrCode_Str(String qrContent, String printTxt, int distance,
                                                    int width, int height) {
            /* posApiHelper.PrintCutQrCode_Str(qrContent, printTxt, distance, width, height,
                    BarcodeFormat.QR_CODE); */
        }

        public synchronized void printBitmap(byte[] bytes) {
            Bitmap bmp1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            posApiHelper.PrintBmp(bmp1);
        }

        /**
         *
         * @param context
         * @param drawableId android resource drawable id
         */
        public synchronized void printBitmapResource(Context context, @DrawableRes int drawableId) {
            Bitmap bmp1 = BitmapFactory.decodeResource(context.getResources(), drawableId);
            posApiHelper.PrintBmp(bmp1);
            //posApiHelper.PrintStr("                                         \n");
        }

        // 40 characters vs 31
        public synchronized void printEndLine() {
            posApiHelper.PrintStr("\n\n");
            //posApiHelper.PrintStr("                                        \n");
            //posApiHelper.PrintStr("                                        \n");
        }

        /**
         * start printing
         */
        public synchronized void printStart(/*Callback<Integer> callback*/) {
            int ret = posApiHelper.PrintStart();
            //if(callback != null) callback.onCallback(ret);
        }

    }

    public interface Callback<T> {
        void onCallback(T t);
    }
}
