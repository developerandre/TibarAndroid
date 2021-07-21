package developer.own.tibar;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.PersistableBundle;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import java.util.ArrayList;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import recieptservice.com.recieptservice.PrinterInterface;
import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "own.channel/tibar";
    private static final PrinterModule printerModule = new PrinterModule();
    private static PrinterModuleRego printerModuleRego;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        printerModuleRego = new PrinterModuleRego(this);
    }
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
      GeneratedPluginRegistrant.registerWith(flutterEngine);
        printerModuleRego = new PrinterModuleRego(this);
       new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(new MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, Result result) {
                        Log.i("call.method", call.method);
                      if (call.method.equals("moveTaskToBack")) {
                        result.success(moveTaskToBack(true));
                      }/*  else if (call.method.equals("testR330")) {
                        try {
                            mAidl.printText("Hello world 2"+"\n");
                            mAidl.printText("Hello world 4"+"\n");
                            mAidl.printText("Hello world 6"+"\n");
                            mAidl.printText("Hello world 8"+"\n");
                        } catch (Exception e) {
                            Log.i("testR330 Error", e.getMessage());
                        } 
                        result.success(1);
                        } */
                      else if (call.method.equals("getSupportPrinters")) {
                        final ArrayList<String> i = printerModuleRego.getSupportPrinters();
                        result.success(i);
                        }else if (call.method.equals("printInitRego")) {
                            final int i = printerModuleRego.printInit();
                            result.success(i);
                        }else if (call.method.equals("PrintCheckStatusRego")) {
                          final int i = printerModuleRego.PrintCheckStatus();
                          result.success(i);
                      }else if (call.method.equals("closeDevices")) {
                          final int i = printerModuleRego.closeDevices();
                          result.success(i);
                      } else if (call.method.equals("printStringRego")) {
                            String text = call.argument("text");
                            printerModuleRego.printString(text);
                            result.success(1);
                        }
                         else if (call.method.equals("PrintCheckStatus")) {
                            final int i = printerModule.PrintCheckStatus();
                            result.success(i);
                        }else if (call.method.equals("printInit")) {
                            printerModule.printInit();
                            result.success(1);
                        } else if (call.method.equals("printSetGray")) {
                            int level = call.argument("level");
                            printerModule.printSetGray(level);
                            result.success(1);
                        } else if (call.method.equals("sysSetPower")) {
                            int mode = call.argument("mode");
                            final int i = printerModule.sysSetPower(mode);
                            result.success(i);
                        } else if (call.method.equals("printSetFontSize")) {
                                    int size = call.argument("size");
                                    int zoom = call.argument("zoom");
                                    printerModule.printSetFontSize(size, zoom);
                                    result.success(1);
                        }else if (call.method.equals("printSetDefaultFont")) {
                            printerModule.printSetDefaultFont();
                            result.success(1);
                        } else if (call.method.equals("printSetAlign")) {
                            int alignment = call.argument("alignment");
                            printerModule.printSetAlign(alignment);
                            result.success(1);
                        } else if (call.method.equals("printString")) {
                            String text = call.argument("text");
                            printerModule.printString(text);
                            result.success(1);
                        } else if (call.method.equals("printBarcode")) {
                            String text = call.argument("text");
                            int width = call.argument("width");
                            int height = call.argument("height");
                            String type = call.argument("type");
                            printerModule.printBarcode(text, width, height, BarcodeFormat.valueOf(type));
                            result.success(1);
                        } else if (call.method.equals("printEndLine")) {
                            printerModule.printEndLine();
                            result.success(1);
                        } else if (call.method.equals("printStart")) {
                            printerModule.printStart();
                            result.success(1);
                        } else if (call.method.equals("printStartRego")) {
                            printerModuleRego.printStart();
                            result.success(1); 
                        } else if (call.method.equals("printEndRego")) {
                            printerModuleRego.printEnd();
                            result.success(1);
                        } else if (call.method.equals("getSupportPageMode")) {
                          String[] modes = printerModuleRego.getSupportPageMode();
                          ArrayList<String> tabs = new ArrayList();

                          for (int i = 0; i < modes.length; i++) {
                              tabs.add(modes[i]);
                          }
                            result.success(tabs);
                        } else {
                            result.notImplemented();
                        }
                        }
                  });
           

      // Imprimante desktop
      Intent intent=new Intent();
        intent.setClassName("recieptservice.com.recieptservice","recieptservice.com.recieptservice.service.PrinterService");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, final IBinder service) {
                PrinterInterface mAidl = PrinterInterface.Stub.asInterface(service);
                }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Service.BIND_AUTO_CREATE);
    }
}
