package tk.srwhiteskull.studentmanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MiCargadorDeImagenesMAGIC {

    private Context mContext;
    private Drawable mDrawable;
    private ImageView mImage;

    public MiCargadorDeImagenesMAGIC(final Context context, ImageView imagen) {
        mContext = context;
        mImage = imagen;

    }

    private void convertimosEnBitmapYguardamos(final String url,final File fichero,final Bitmap.CompressFormat formato,final int calidad) {
        mDrawable = null;
        new Thread(){
            public void run() {
                // Cargamos el Drawdable
                try {
                    mDrawable = getDrawableFromUrl(mContext, url);

                    // convertimos a Bitmap
                    Bitmap bitmap = null;

                    if (mDrawable instanceof BitmapDrawable) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) mDrawable;
                        if(bitmapDrawable.getBitmap() != null) {
                            bitmap = bitmapDrawable.getBitmap();
                        }
                    } else {
                        if (mDrawable!=null) {
                            if (mDrawable.getIntrinsicWidth() <= 0 || mDrawable.getIntrinsicHeight() <= 0) {
                                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
                            } else {
                                bitmap = Bitmap.createBitmap(mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                            }
                            Canvas canvas = new Canvas(bitmap);
                            mDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            mDrawable.draw(canvas);
                        }
                    }

                    if (bitmap!=null) {
                        Registros.bitmaps.put(url,bitmap);
                        // Guradamos en fichero
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(fichero);
                            bitmap.compress(formato, calidad, fos);
                            fos.close();
                        } catch (IOException e) {
                            Log.e("app", e.getMessage());
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDrawable!=null) mImage.setImageDrawable(mDrawable);
                            else mImage.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.anonimo));
                        }
                    });
                } catch (MalformedURLException e) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImage.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.anonimo));
                        }
                    });
                } catch (IOException e) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImage.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.anonimo));
                        }
                    });
                }

            };
        }.start();

    }

    private void cargamosImagen(String url, String nombreFichero){
        Bitmap bitmap=BitmapFactory.decodeFile(mContext.getFilesDir()+"/"+nombreFichero);
        if (bitmap!=null) {
            Registros.bitmaps.put(url, bitmap);
            mImage.setImageBitmap(bitmap);
        } else {
            mImage.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.anonimo));
        }
    }

    public void setImageDrawable(String imageUrl) {
        String nombreFichero= imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        File fichero = new File(mContext.getFilesDir(), nombreFichero);
        // comprobamos si existe el fichero
        if(fichero.exists()) {
            // de exitir el fichero comprobamos su fecha de creacion
            // si la fecha de creacion es mayor de 24 horas lo cargamos de la web y lo guardamos
            if ((System.currentTimeMillis()-fichero.lastModified())>(24*3600000)) {
                convertimosEnBitmapYguardamos(imageUrl, fichero, Bitmap.CompressFormat.JPEG, 50);
            // en caso de ser inferior a 24 horas cargamos el ficher
            } else {
                cargamosImagen(imageUrl, nombreFichero);
            }
        // de no existir guardamos la imagen en un fichero
        } else {
            convertimosEnBitmapYguardamos(imageUrl, fichero, Bitmap.CompressFormat.JPEG, 50);
        }

    }

    private static Drawable getDrawableFromUrl(Context mContext, final String url) throws IOException, MalformedURLException {
        Drawable resultado;
        try {
            resultado = MiCargadorDeImagenesMAGIC.resizeImage(Drawable.createFromStream(((InputStream) new URL(url).getContent()), "name"),
                    MiCargadorDeImagenesMAGIC.dipToPixels(mContext, 80));
            //resultado = MiCargadorDeImagenesMAGIC.scaleImage(Drawable.createFromStream(((InputStream) new URL(url).getContent()), "name"), 0.25f);
            //resultado = Drawable.createFromStream(((InputStream) new URL(url).getContent()), "name");
        } catch (OutOfMemoryError om){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                resultado= Registros.actividad.getDrawable(R.drawable.anonimo);
            } else {
                resultado = ContextCompat.getDrawable(Registros.actividad,R.drawable.anonimo);
            }
        }
        return resultado;
    }

    private static Drawable resizeImage (Drawable image, int tam) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, tam, tam, false);

        image = new BitmapDrawable(Registros.actividad.getResources(), bitmapResized);

        return image;

    }

    public static int dipToPixels(Context context, int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);//COMPLEX_UNIT_DIP
    }


    private static Drawable scaleImage (Drawable image, float scaleFactor) {
        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(Registros.actividad.getResources(), bitmapResized);

        return image;

    }

}
