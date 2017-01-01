package android.support.v4.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentInfo;
import android.print.PrintDocumentInfo.Builder;
import android.print.PrintManager;
import java.io.FileNotFoundException;

class PrintHelperKitkat
{
  public static final int COLOR_MODE_COLOR = 2;
  public static final int COLOR_MODE_MONOCHROME = 1;
  private static final String LOG_TAG = "PrintHelperKitkat";
  private static final int MAX_PRINT_SIZE = 3500;
  public static final int ORIENTATION_LANDSCAPE = 1;
  public static final int ORIENTATION_PORTRAIT = 2;
  public static final int SCALE_MODE_FILL = 2;
  public static final int SCALE_MODE_FIT = 1;
  int mColorMode = 2;
  final Context mContext;
  BitmapFactory.Options mDecodeOptions = null;
  private final Object mLock = new Object();
  int mOrientation = 1;
  int mScaleMode = 2;
  
  PrintHelperKitkat(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private Matrix getMatrix(int paramInt1, int paramInt2, RectF paramRectF, int paramInt3)
  {
    Matrix localMatrix = new Matrix();
    float f = paramRectF.width() / paramInt1;
    if (paramInt3 != 2) {
      f = Math.min(f, paramRectF.height() / paramInt2);
    } else {
      f = Math.max(f, paramRectF.height() / paramInt2);
    }
    localMatrix.postScale(f, f);
    localMatrix.postTranslate((paramRectF.width() - f * paramInt1) / 2.0F, (paramRectF.height() - f * paramInt2) / 2.0F);
    return localMatrix;
  }
  
  /* Error */
  private Bitmap loadBitmap(Uri paramUri, BitmapFactory.Options paramOptions)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +10 -> 11
    //   4: aload_0
    //   5: getfield 52	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
    //   8: ifnonnull +13 -> 21
    //   11: new 103	java/lang/IllegalArgumentException
    //   14: dup
    //   15: ldc 105
    //   17: invokespecial 108	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   20: athrow
    //   21: aconst_null
    //   22: astore 4
    //   24: aload_0
    //   25: getfield 52	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
    //   28: invokevirtual 114	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   31: aload_1
    //   32: invokevirtual 120	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   35: astore 4
    //   37: aload 4
    //   39: aconst_null
    //   40: aload_2
    //   41: invokestatic 126	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   44: astore_3
    //   45: aload 4
    //   47: ifnull +8 -> 55
    //   50: aload 4
    //   52: invokevirtual 131	java/io/InputStream:close	()V
    //   55: aload_3
    //   56: areturn
    //   57: astore 4
    //   59: ldc 20
    //   61: ldc -123
    //   63: aload 4
    //   65: invokestatic 139	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   68: pop
    //   69: goto -14 -> 55
    //   72: astore_3
    //   73: aload 4
    //   75: ifnull +8 -> 83
    //   78: aload 4
    //   80: invokevirtual 131	java/io/InputStream:close	()V
    //   83: aload_3
    //   84: athrow
    //   85: astore 4
    //   87: ldc 20
    //   89: ldc -123
    //   91: aload 4
    //   93: invokestatic 139	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   96: pop
    //   97: goto -14 -> 83
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	100	0	this	PrintHelperKitkat
    //   0	100	1	paramUri	Uri
    //   0	100	2	paramOptions	BitmapFactory.Options
    //   44	12	3	localBitmap	Bitmap
    //   72	12	3	localObject	Object
    //   22	29	4	localInputStream	java.io.InputStream
    //   57	22	4	localIOException1	java.io.IOException
    //   85	7	4	localIOException2	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   50	55	57	java/io/IOException
    //   24	45	72	finally
    //   78	83	85	java/io/IOException
  }
  
  private Bitmap loadConstrainedBitmap(Uri paramUri, int paramInt)
    throws FileNotFoundException
  {
    ??? = null;
    if ((paramInt <= 0) || (paramUri == null) || (this.mContext == null)) {
      throw new IllegalArgumentException("bad argument to getScaledBitmap");
    }
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    loadBitmap(paramUri, localOptions);
    int i = localOptions.outWidth;
    int m = localOptions.outHeight;
    if ((i <= 0) || (m <= 0)) {}
    int j;
    do
    {
      return (Bitmap)???;
      int k = Math.max(i, m);
      j = 1;
      while (k > paramInt)
      {
        k >>>= 1;
        j <<= 1;
      }
    } while ((j <= 0) || (Math.min(i, m) / j <= 0));
    synchronized (this.mLock)
    {
      this.mDecodeOptions = new BitmapFactory.Options();
      this.mDecodeOptions.inMutable = true;
      this.mDecodeOptions.inSampleSize = j;
      ??? = this.mDecodeOptions;
    }
    try
    {
      ??? = loadBitmap(paramUri, (BitmapFactory.Options)???);
      ??? = ???;
      synchronized (this.mLock)
      {
        this.mDecodeOptions = null;
      }
      localObject5 = finally;
      throw ((Throwable)localObject5);
    }
    finally {}
  }
  
  public int getColorMode()
  {
    return this.mColorMode;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public int getScaleMode()
  {
    return this.mScaleMode;
  }
  
  public void printBitmap(final String paramString, final Bitmap paramBitmap, final OnPrintFinishCallback paramOnPrintFinishCallback)
  {
    if (paramBitmap != null)
    {
      final int i = this.mScaleMode;
      PrintManager localPrintManager = (PrintManager)this.mContext.getSystemService("print");
      Object localObject = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;
      if (paramBitmap.getWidth() > paramBitmap.getHeight()) {
        localObject = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE;
      }
      localObject = new PrintAttributes.Builder().setMediaSize((PrintAttributes.MediaSize)localObject).setColorMode(this.mColorMode).build();
      localPrintManager.print(paramString, new PrintDocumentAdapter()
      {
        private PrintAttributes mAttributes;
        
        public void onFinish()
        {
          if (paramOnPrintFinishCallback != null) {
            paramOnPrintFinishCallback.onFinish();
          }
        }
        
        public void onLayout(PrintAttributes paramAnonymousPrintAttributes1, PrintAttributes paramAnonymousPrintAttributes2, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
        {
          int i = 1;
          this.mAttributes = paramAnonymousPrintAttributes2;
          PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(paramString).setContentType(i).setPageCount(i).build();
          if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {
            i = 0;
          }
          paramAnonymousLayoutResultCallback.onLayoutFinished(localPrintDocumentInfo, i);
        }
        
        /* Error */
        public void onWrite(android.print.PageRange[] paramAnonymousArrayOfPageRange, android.os.ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, android.print.PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
        {
          // Byte code:
          //   0: new 79	android/print/pdf/PrintedPdfDocument
          //   3: dup
          //   4: aload_0
          //   5: getfield 25	android/support/v4/print/PrintHelperKitkat$1:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   8: getfield 83	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
          //   11: aload_0
          //   12: getfield 45	android/support/v4/print/PrintHelperKitkat$1:mAttributes	Landroid/print/PrintAttributes;
          //   15: invokespecial 86	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
          //   18: astore 5
          //   20: aload 5
          //   22: iconst_1
          //   23: invokevirtual 90	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
          //   26: astore 6
          //   28: new 92	android/graphics/RectF
          //   31: dup
          //   32: aload 6
          //   34: invokevirtual 98	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
          //   37: invokevirtual 104	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
          //   40: invokespecial 107	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
          //   43: astore 7
          //   45: aload_0
          //   46: getfield 25	android/support/v4/print/PrintHelperKitkat$1:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   49: aload_0
          //   50: getfield 29	android/support/v4/print/PrintHelperKitkat$1:val$bitmap	Landroid/graphics/Bitmap;
          //   53: invokevirtual 113	android/graphics/Bitmap:getWidth	()I
          //   56: aload_0
          //   57: getfield 29	android/support/v4/print/PrintHelperKitkat$1:val$bitmap	Landroid/graphics/Bitmap;
          //   60: invokevirtual 116	android/graphics/Bitmap:getHeight	()I
          //   63: aload 7
          //   65: aload_0
          //   66: getfield 31	android/support/v4/print/PrintHelperKitkat$1:val$fittingMode	I
          //   69: invokestatic 120	android/support/v4/print/PrintHelperKitkat:access$000	(Landroid/support/v4/print/PrintHelperKitkat;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
          //   72: astore 7
          //   74: aload 6
          //   76: invokevirtual 124	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
          //   79: aload_0
          //   80: getfield 29	android/support/v4/print/PrintHelperKitkat$1:val$bitmap	Landroid/graphics/Bitmap;
          //   83: aload 7
          //   85: aconst_null
          //   86: invokevirtual 130	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
          //   89: aload 5
          //   91: aload 6
          //   93: invokevirtual 134	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
          //   96: aload 5
          //   98: new 136	java/io/FileOutputStream
          //   101: dup
          //   102: aload_2
          //   103: invokevirtual 142	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
          //   106: invokespecial 145	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
          //   109: invokevirtual 149	android/print/pdf/PrintedPdfDocument:writeTo	(Ljava/io/OutputStream;)V
          //   112: iconst_1
          //   113: anewarray 151	android/print/PageRange
          //   116: astore 6
          //   118: aload 6
          //   120: iconst_0
          //   121: getstatic 155	android/print/PageRange:ALL_PAGES	Landroid/print/PageRange;
          //   124: aastore
          //   125: aload 4
          //   127: aload 6
          //   129: invokevirtual 161	android/print/PrintDocumentAdapter$WriteResultCallback:onWriteFinished	([Landroid/print/PageRange;)V
          //   132: aload 5
          //   134: ifnull +8 -> 142
          //   137: aload 5
          //   139: invokevirtual 164	android/print/pdf/PrintedPdfDocument:close	()V
          //   142: aload_2
          //   143: ifnull +7 -> 150
          //   146: aload_2
          //   147: invokevirtual 165	android/os/ParcelFileDescriptor:close	()V
          //   150: return
          //   151: astore 6
          //   153: ldc -89
          //   155: ldc -87
          //   157: aload 6
          //   159: invokestatic 175	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   162: pop
          //   163: aload 4
          //   165: aconst_null
          //   166: invokevirtual 179	android/print/PrintDocumentAdapter$WriteResultCallback:onWriteFailed	(Ljava/lang/CharSequence;)V
          //   169: goto -37 -> 132
          //   172: astore 6
          //   174: aload 5
          //   176: ifnull +8 -> 184
          //   179: aload 5
          //   181: invokevirtual 164	android/print/pdf/PrintedPdfDocument:close	()V
          //   184: aload_2
          //   185: ifnull +7 -> 192
          //   188: aload_2
          //   189: invokevirtual 165	android/os/ParcelFileDescriptor:close	()V
          //   192: aload 6
          //   194: athrow
          //   195: pop
          //   196: goto -46 -> 150
          //   199: pop
          //   200: goto -8 -> 192
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	203	0	this	1
          //   0	203	1	paramAnonymousArrayOfPageRange	android.print.PageRange[]
          //   0	203	2	paramAnonymousParcelFileDescriptor	android.os.ParcelFileDescriptor
          //   0	203	3	paramAnonymousCancellationSignal	CancellationSignal
          //   0	203	4	paramAnonymousWriteResultCallback	android.print.PrintDocumentAdapter.WriteResultCallback
          //   18	162	5	localPrintedPdfDocument	android.print.pdf.PrintedPdfDocument
          //   26	102	6	localObject1	Object
          //   151	7	6	localIOException1	java.io.IOException
          //   172	21	6	localObject2	Object
          //   43	41	7	localObject3	Object
          //   195	1	10	localIOException2	java.io.IOException
          //   199	1	11	localIOException3	java.io.IOException
          // Exception table:
          //   from	to	target	type
          //   96	132	151	java/io/IOException
          //   20	96	172	finally
          //   96	132	172	finally
          //   153	169	172	finally
          //   146	150	195	java/io/IOException
          //   188	192	199	java/io/IOException
        }
      }, (PrintAttributes)localObject);
    }
  }
  
  public void printBitmap(final String paramString, final Uri paramUri, final OnPrintFinishCallback paramOnPrintFinishCallback)
    throws FileNotFoundException
  {
    PrintDocumentAdapter local2 = new PrintDocumentAdapter()
    {
      AsyncTask<Uri, Boolean, Bitmap> loadBitmap;
      private PrintAttributes mAttributes;
      Bitmap mBitmap = null;
      
      private void cancelLoad()
      {
        synchronized (PrintHelperKitkat.this.mLock)
        {
          if (PrintHelperKitkat.this.mDecodeOptions != null)
          {
            PrintHelperKitkat.this.mDecodeOptions.requestCancelDecode();
            PrintHelperKitkat.this.mDecodeOptions = null;
          }
          return;
        }
      }
      
      public void onFinish()
      {
        super.onFinish();
        cancelLoad();
        this.loadBitmap.cancel(true);
        if (paramOnPrintFinishCallback != null) {
          paramOnPrintFinishCallback.onFinish();
        }
      }
      
      public void onLayout(final PrintAttributes paramAnonymousPrintAttributes1, final PrintAttributes paramAnonymousPrintAttributes2, final CancellationSignal paramAnonymousCancellationSignal, final PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
      {
        int i = 1;
        if (!paramAnonymousCancellationSignal.isCanceled())
        {
          if (this.mBitmap == null)
          {
            this.loadBitmap = new AsyncTask()
            {
              protected Bitmap doInBackground(Uri... paramAnonymous2VarArgs)
              {
                try
                {
                  localBitmap = PrintHelperKitkat.this.loadConstrainedBitmap(PrintHelperKitkat.2.this.val$imageFile, 3500);
                  localBitmap = localBitmap;
                }
                catch (FileNotFoundException localFileNotFoundException)
                {
                  for (;;)
                  {
                    Bitmap localBitmap = null;
                  }
                }
                return localBitmap;
              }
              
              protected void onCancelled(Bitmap paramAnonymous2Bitmap)
              {
                paramAnonymousLayoutResultCallback.onLayoutCancelled();
              }
              
              protected void onPostExecute(Bitmap paramAnonymous2Bitmap)
              {
                int i = 1;
                super.onPostExecute(paramAnonymous2Bitmap);
                PrintHelperKitkat.2.this.mBitmap = paramAnonymous2Bitmap;
                if (paramAnonymous2Bitmap == null)
                {
                  paramAnonymousLayoutResultCallback.onLayoutFailed(null);
                }
                else
                {
                  PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(PrintHelperKitkat.2.this.val$jobName).setContentType(i).setPageCount(i).build();
                  if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {
                    i = 0;
                  }
                  paramAnonymousLayoutResultCallback.onLayoutFinished(localPrintDocumentInfo, i);
                }
              }
              
              protected void onPreExecute()
              {
                paramAnonymousCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
                {
                  public void onCancel()
                  {
                    PrintHelperKitkat.2.this.cancelLoad();
                    PrintHelperKitkat.2.1.this.cancel(false);
                  }
                });
              }
            };
            this.loadBitmap.execute(new Uri[0]);
            this.mAttributes = paramAnonymousPrintAttributes2;
          }
          else
          {
            PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(paramString).setContentType(i).setPageCount(i).build();
            if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {
              i = 0;
            }
            paramAnonymousLayoutResultCallback.onLayoutFinished(localPrintDocumentInfo, i);
          }
        }
        else
        {
          paramAnonymousLayoutResultCallback.onLayoutCancelled();
          this.mAttributes = paramAnonymousPrintAttributes2;
        }
      }
      
      /* Error */
      public void onWrite(android.print.PageRange[] paramAnonymousArrayOfPageRange, android.os.ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, android.print.PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
      {
        // Byte code:
        //   0: new 133	android/print/pdf/PrintedPdfDocument
        //   3: dup
        //   4: aload_0
        //   5: getfield 32	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
        //   8: getfield 137	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
        //   11: aload_0
        //   12: getfield 96	android/support/v4/print/PrintHelperKitkat$2:mAttributes	Landroid/print/PrintAttributes;
        //   15: invokespecial 140	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
        //   18: astore 5
        //   20: aload 5
        //   22: iconst_1
        //   23: invokevirtual 144	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
        //   26: astore 6
        //   28: new 146	android/graphics/RectF
        //   31: dup
        //   32: aload 6
        //   34: invokevirtual 152	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
        //   37: invokevirtual 158	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
        //   40: invokespecial 161	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
        //   43: astore 7
        //   45: aload_0
        //   46: getfield 32	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
        //   49: aload_0
        //   50: getfield 45	android/support/v4/print/PrintHelperKitkat$2:mBitmap	Landroid/graphics/Bitmap;
        //   53: invokevirtual 167	android/graphics/Bitmap:getWidth	()I
        //   56: aload_0
        //   57: getfield 45	android/support/v4/print/PrintHelperKitkat$2:mBitmap	Landroid/graphics/Bitmap;
        //   60: invokevirtual 170	android/graphics/Bitmap:getHeight	()I
        //   63: aload 7
        //   65: aload_0
        //   66: getfield 40	android/support/v4/print/PrintHelperKitkat$2:val$fittingMode	I
        //   69: invokestatic 174	android/support/v4/print/PrintHelperKitkat:access$000	(Landroid/support/v4/print/PrintHelperKitkat;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
        //   72: astore 7
        //   74: aload 6
        //   76: invokevirtual 178	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
        //   79: aload_0
        //   80: getfield 45	android/support/v4/print/PrintHelperKitkat$2:mBitmap	Landroid/graphics/Bitmap;
        //   83: aload 7
        //   85: aconst_null
        //   86: invokevirtual 184	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
        //   89: aload 5
        //   91: aload 6
        //   93: invokevirtual 188	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
        //   96: aload 5
        //   98: new 190	java/io/FileOutputStream
        //   101: dup
        //   102: aload_2
        //   103: invokevirtual 196	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
        //   106: invokespecial 199	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
        //   109: invokevirtual 203	android/print/pdf/PrintedPdfDocument:writeTo	(Ljava/io/OutputStream;)V
        //   112: iconst_1
        //   113: anewarray 205	android/print/PageRange
        //   116: astore 6
        //   118: aload 6
        //   120: iconst_0
        //   121: getstatic 209	android/print/PageRange:ALL_PAGES	Landroid/print/PageRange;
        //   124: aastore
        //   125: aload 4
        //   127: aload 6
        //   129: invokevirtual 215	android/print/PrintDocumentAdapter$WriteResultCallback:onWriteFinished	([Landroid/print/PageRange;)V
        //   132: aload 5
        //   134: ifnull +8 -> 142
        //   137: aload 5
        //   139: invokevirtual 218	android/print/pdf/PrintedPdfDocument:close	()V
        //   142: aload_2
        //   143: ifnull +7 -> 150
        //   146: aload_2
        //   147: invokevirtual 219	android/os/ParcelFileDescriptor:close	()V
        //   150: return
        //   151: astore 6
        //   153: ldc -35
        //   155: ldc -33
        //   157: aload 6
        //   159: invokestatic 229	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   162: pop
        //   163: aload 4
        //   165: aconst_null
        //   166: invokevirtual 233	android/print/PrintDocumentAdapter$WriteResultCallback:onWriteFailed	(Ljava/lang/CharSequence;)V
        //   169: goto -37 -> 132
        //   172: astore 6
        //   174: aload 5
        //   176: ifnull +8 -> 184
        //   179: aload 5
        //   181: invokevirtual 218	android/print/pdf/PrintedPdfDocument:close	()V
        //   184: aload_2
        //   185: ifnull +7 -> 192
        //   188: aload_2
        //   189: invokevirtual 219	android/os/ParcelFileDescriptor:close	()V
        //   192: aload 6
        //   194: athrow
        //   195: pop
        //   196: goto -46 -> 150
        //   199: pop
        //   200: goto -8 -> 192
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	203	0	this	2
        //   0	203	1	paramAnonymousArrayOfPageRange	android.print.PageRange[]
        //   0	203	2	paramAnonymousParcelFileDescriptor	android.os.ParcelFileDescriptor
        //   0	203	3	paramAnonymousCancellationSignal	CancellationSignal
        //   0	203	4	paramAnonymousWriteResultCallback	android.print.PrintDocumentAdapter.WriteResultCallback
        //   18	162	5	localPrintedPdfDocument	android.print.pdf.PrintedPdfDocument
        //   26	102	6	localObject1	Object
        //   151	7	6	localIOException1	java.io.IOException
        //   172	21	6	localObject2	Object
        //   43	41	7	localObject3	Object
        //   195	1	10	localIOException2	java.io.IOException
        //   199	1	11	localIOException3	java.io.IOException
        // Exception table:
        //   from	to	target	type
        //   96	132	151	java/io/IOException
        //   20	96	172	finally
        //   96	132	172	finally
        //   153	169	172	finally
        //   146	150	195	java/io/IOException
        //   188	192	199	java/io/IOException
      }
    };
    PrintManager localPrintManager = (PrintManager)this.mContext.getSystemService("print");
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder();
    localBuilder.setColorMode(this.mColorMode);
    if (this.mOrientation != 1)
    {
      if (this.mOrientation == 2) {
        localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
      }
    }
    else {
      localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
    }
    localPrintManager.print(paramString, local2, localBuilder.build());
  }
  
  public void setColorMode(int paramInt)
  {
    this.mColorMode = paramInt;
  }
  
  public void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
  }
  
  public void setScaleMode(int paramInt)
  {
    this.mScaleMode = paramInt;
  }
  
  public static abstract interface OnPrintFinishCallback
  {
    public abstract void onFinish();
  }
}


/* Location:              E:\classes_dex2jar.jar!\android\support\v4\print\PrintHelperKitkat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */