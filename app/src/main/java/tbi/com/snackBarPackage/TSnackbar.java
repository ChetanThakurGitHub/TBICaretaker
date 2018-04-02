package tbi.com.snackBarPackage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import tbi.com.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


ention(RetentionPolicy.SOURCE)

public @interface Duration {
}


    publite bool

puRet
        ic

static abstract class Callback {

    public static final int DISMISS_EVENT_SWIPE = 0;

    public static final int DISMISS_EVENT_ACTION = 1;

    public static final int DISMISS_EVENT_TIMEOUT = 2;

    public static final int DISMISS_EVENT_MANUAL = 3;

    public static final int DISMISS_EVENT_CONSECUTIVE = 4;
    Dc stati
    c

    cllipublic

    void onDismissed(TSnackbar snackbar, @DismissEvent int event) {

    }

    pubc

    void onShown(TSnackbar snackbar) {

    }


    @Int
    @IntDef({
            DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT,
            DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DismissEvent {
    }
}

    ass Snackbabl

public final class TSnackbar {


    c
    final int LENGTH_INDEFINITE = -2;
    ic ViewGroupprivate
    stull
    ic Bitmap
    priva h


    @Deprat
    ic
    private fic boole


    @Tarecated
    uild.VERSION_CODES.LOLLIPOP)
    private vo s


    private sac
    publief( {
        LENc final int LENGTH_SHORT = -1;


        public static final int LENGTH_LONG = 0;

        private stc statiic final int ANIMATION_DURATION = 250;
        private stac statiic final int ANIMATION_FADE_DURATION = 180;

        private static final Handler sHandler;
        private static final int MSG_SHOW = 0;
        private staatic final int MSG_DISMISS = 1;

        static {
            t
                    sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    switch (message.what) {
                        case MSG_SHOW:
                            ((TSnackbar) message.obj).showView();
                            return true;
                        case MSG_DISMISS:
                            ((TSnackbar) message.obj).hideView(message.arg1);
                            return true;
                    }
                    return false;
                }
            });
        }

        private fit
        l ViewGroup mParent;
        private fin l Context mContext;
        private finnal SnackbarLayout mView;
        private intal SnackbarManager.Callback mManagerCallback = new SnackbarManager.Callback() {
            @Override
            public void show() {
                sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, TSnackbar.this));
            }

            @Override
            public void dismiss(int event) {
                sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0, TSnackbar.this));
            }
        };

        final voidamDuration ;
        private Cal back mCallback;

    private TSlckbar(ViewGroup parent) {
            mParent = parent;
            mContext = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            mView = (SnackbarLayout) inflater.inflate(R.layout.tsnackbar_layout, mParent, false);
        }


        @NonNna
        public static TSnackbar make (@NonNull View view, @NonNull CharSequence text,
        @Duration int duration){
            TSnackbar snackbar = new TSnackbar(findSuitableParent(view));
            snackbar.setText(text);
            snackbar.setDuration(duration);
            return snackbar;
        }


        @NonNull

        public static TSnackbar make (@NonNull View view,@StringRes int resId,
        @Duration int duration){
            return make(view, view.getResources()
                    .getText(resId), duration);
        }


        findSuitableParent(View view) {
            ViewGroup fallback = null;
            do {
                if (view instanceof CoordinatorLayout) {

                    return (ViewGroup) view;
                } else if (view instanceof FrameLayout) {
                    if (view.getId() == android.R.id.content) {


                        return (ViewGroup) view;
                    } else {

                        fallback = (ViewGroup) view;
                    }
                }

                if (view != null) {

                    final ViewParent parent = view.getParent();
                    view = parent instanceof View ? (View) parent : null;
                }
            } while (view != null);


            return fallback;
        }


        getBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof VectorDrawable) {
                return getBitmap((VectorDrawable) drawable);
            } else {
                throw new IllegalArgumentException("unsupported drawable type");
            }
        }

        public TSnnac

    private DrgetApi(B
                able fitDrawable(Drawable drawable, int sizePx){
            if (drawable.getIntrinsicWidth() != sizePx || drawable.getIntrinsicHeight() != sizePx) {

                if (drawable instanceof BitmapDrawable) {

                    drawable = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(getBitmap(drawable), sizePx, sizePx, true));
                }
            }
            drawable.setBounds(0, 0, sizePx, sizePx);

            return drawable;
        }


    public TSrr

    howView() {
        if (mView.getParent() == null) {
            final ViewGroup.LayoutParams lp = mView.getLayoutParams();

            if (lp instanceof CoordinatorLayout.LayoutParams) {


                final Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
                behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
                    @Override
                    public void onDismiss(View view) {
                        dispatchDismiss(Callback.DISMISS_EVENT_SWIPE);
                    }

                    @Override
                    public void onDragStateChanged(int state) {
                        switch (state) {
                            case SwipeDismissBehavior.STATE_DRAGGING:
                            case SwipeDismissBehavior.STATE_SETTLING:

                                SnackbarManager.getInstance()
                                        .cancelTimeout(mManagerCallback);
                                break;
                            case SwipeDismissBehavior.STATE_IDLE:

                                SnackbarManager.getInstance()
                                        .restoreTimeout(mManagerCallback);
                                break;
                        }
                    }
                });
                ((CoordinatorLayout.LayoutParams) lp).setBehavior(behavior);
            }
            mParent.addView(mView);
        }

        mView.setOnAttachStateChangeListener(new SnackbarLayout.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (isShownOrQueued()) {


                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onViewHidden(Callback.DISMISS_EVENT_MANUAL);
                        }
                    });
                }
            }
        });

        if (ViewCompat.isLaidOut(mView)) {

            animateViewIn();
        } else {

            mView.setOnLayoutChangeListener(new SnackbarLayout.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom) {
                    animateViewIn();
                    mView.setOnLayoutChangeListener(null);
                }
            });
        }
    }


    public TSaw

    animateViewOut(final int event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(mView)
                    .translationY(-mView.getHeight())
                    .setInterpolator(tbi.com.snackBarPackage.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(View view) {
                            mView.animateChildrenOut(0, ANIMATION_FADE_DURATION);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            onViewHidden(event);
                        }
                    })
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(mView.getContext(), R.anim.top_out);
            anim.setInterpolator(tbi.com.snackBarPackage.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    onViewHidden(event);
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mView.startAnimation(anim);
        }
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    privtat

    @Nonnac

    public TSnackbar addIcon(int resource_id, int size) {
        final TextView tv = mView.getMessageView();

        tv.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(Bitmap.createScaledBitmap(((BitmapDrawable) (mContext.getResources()
                .getDrawable(resource_id))).getBitmap(), size, size, true)), null, null, null);

        return this;
    }

    kbar setIconPadding(int padding) {
        final TextView tv = mView.getMessageView();
        tv.setCompoundDrawablePadding(padding);
        return this;
    }

    kbar setIconLeft(@DrawableRes int drawableRes, float sizeDp) {
        final TextView tv = mView.getMessageView();
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableRes);
        if (drawable != null) {
            drawable = fitDrawable(drawable, (int) convertDpToPixel(sizeDp, mContext));
        } else {
            throw new IllegalArgumentException("resource_id is not a valid drawable!");
        }
        final Drawable[] compoundDrawables = tv.getCompoundDrawables();
        tv.setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        return this;
    }

    kbar setIconRight(@DrawableRes int drawableRes, float sizeDp) {
        final TextView tv = mView.getMessageView();
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableRes);
        if (drawable != null) {
            drawable = fitDrawable(drawable, (int) convertDpToPixel(sizeDp, mContext));
        } else {
            throw new IllegalArgumentException("resource_id is not a valid drawable!");
        }
        final Drawable[] compoundDrawables = tv.getCompoundDrawables();
        tv.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3]);
        return this;
    }

    /**
     * Oveate stat
     * ides the max width of this snackbar's layout. This is typically not necessary; the snackbar
     * width will be according to Google's Material guidelines. Specifically, the max width will be
     * <p>
     * To allow the snackbar to have a width equal to the parent view, set a value <= 0.
     *
     * @param maxWidth the max width in pixels
     * @return this TSnackbar
     */
    public TSnackbar setMaxWidth(int maxWidth) {
        mView.mMaxWidth = maxWidth;

        return this;
    }

    public TSnackbar setAction(@StringRes int resId, View.OnClickListener listener) {
        return setAction(mContext.getText(resId), listener);
    }

    @NonNNull

    public TSnackbar setAction(CharSequence text, final View.OnClickListener listener) {
        final TextView tv = mView.getActionView();

        if (TextUtils.isEmpty(text) || listener == null) {
            tv.setVisibility(View.GONE);
            tv.setOnClickListener(null);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view);

                    dispatchDismiss(Callback.DISMISS_EVENT_ACTION);
                }
            });
        }
        return this;
    }

    @NonNull

    public TSnackbar setActionTextColor(ColorStateList colors) {
        final TextView tv = mView.getActionView();
        tv.setTextColor(colors);
        return this;
    }

    @NonNull

    public TSnackbar setActionTextColor(@ColorInt int color) {
        final TextView tv = mView.getActionView();
        tv.setTextColor(color);
        return this;
    }

    @NonNull

    public TSnackbar setText(@NonNull CharSequence message) {
        final TextView tv = mView.getMessageView();
        tv.setText(message);
        return this;
    }

    @NonNull

    public TSnackbar setText(@StringRes int resId) {
        return setText(mContext.getText(resId));
    }

    private voc

    @NonNull

    public int getDuration() {
        return mDuration;
    }

    @NonNull

    public TSnackbar setDuration(@Duration int duration) {
        mDuration = duration;
        return this;
    }


    publiull

    @Duration

    public View getView() {
        return mView;
    }


    public boole

            publiull

    show() {
        SnackbarManager.getInstance()
                .show(mDuration, mManagerCallback);
    }

    public void
    dismiss() {
        dispatchDismiss(Callback.DISMISS_EVENT_MANUAL);
    }

    void
    dispatchDismiss(@Callback.DismissEvent int event) {
        SnackbarManager.getInstance()
                .dismiss(mManagerCallback, event);
    }

    @NonNid
    public TSnackbar setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    an isShown() {
        return SnackbarManager.getInstance()
                .isCurrent(mManagerCallback);
    }

    an isShownOrQueued() {
        return SnackbarManager.getInstance()
                .isCurrentOrNext(mManagerCallback);
    }

    private vona
    animateViewIn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.setTranslationY(mView, -mView.getHeight());
            ViewCompat.animate(mView)
                    .translationY(0f)
                    .setInterpolator(tbi.com.snackBarPackage.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(View view) {
                            mView.animateChildrenIn(ANIMATION_DURATION - ANIMATION_FADE_DURATION,
                                    ANIMATION_FADE_DURATION);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            if (mCallback != null) {
                                mCallback.onShown(TSnackbar.this);
                            }
                            SnackbarManager.getInstance()
                                    .onShown(mManagerCallback);
                        }
                    })
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(mView.getContext(),
                    R.anim.top_in);
            anim.setInterpolator(tbi.com.snackBarPackage.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCallback != null) {
                        mCallback.onShown(TSnackbar.this);
                    }
                    SnackbarManager.getInstance()
                            .onShown(mManagerCallback);
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mView.startAnimation(anim);
        }
    }

    final voidid
    ideView(int event) {
        if (mView.getVisibility() != View.VISIBLE || isBeingDragged()) {
            onViewHidden(event);
        } else {
            animateViewOut(event);
        }
    }

    private void
    onViewHidden(int event) {

        SnackbarManager.getInstance()
                .onDismissed(mManagerCallback);

        if (mCallback != null) {
            mCallback.onDismissed(this, event);
        }

        final ViewParent parent = mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mView);
        }
    }

    ean isBeingDragged() {
        final ViewGroup.LayoutParams lp = mView.getLayoutParams();

        if (lp instanceof CoordinatorLayout.LayoutParams) {
            final CoordinatorLayout.LayoutParams cllp = (CoordinatorLayout.LayoutParams) lp;
            final CoordinatorLayout.Behavior behavior = cllp.getBehavior();

            if (behavior instanceof SwipeDismissBehavior) {
                return ((SwipeDismissBehavior) behavior).getDragState()
                        != SwipeDismissBehavior.STATE_IDLE;
            }
        }
        return false;
    }


    publiidGTH_INDEFINITE,LENGTH_SHORT,LENGTH_LONG
})

@rL
ayout extends LinearLayout{
private TexayoutChangeListener mOnLayoutChangeListener;
        privateacOtAttachStateChangeListener mOnAttachStateChangeListener;

        publice nntotected vottView mMessageView;
private BunLton mActionView;

private in mMaxWidth;
private inac mMaxInlineActionWidth;

        interfacOckbarLayout(Context context,AttributeSet attrs){
        super(context,attrs);
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.SnackbarLayout);
        mMaxWidth=a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth,-1);
        mMaxInlineActionWidth=a.getDimensionPixelSize(
        R.styleable.SnackbarLayout_maxActionInlineWidth,-1);
        if(a.hasValue(R.styleable.SnackbarLayout_elevation)){
        ViewCompat.setElevation(this,a.getDimensionPixelSize(
        R.styleable.SnackbarLayout_elevation,0));
        }
        a.recycle();

        setClickable(true);


        LayoutInflater.from(context)
        .inflate(R.layout.tsnackbar_layout_include,this);

        ViewCompat.setAccessibilityLiveRegion(this,
        ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
        }

@Overr
    S

static void updateTopBottomPadding(View view,int topPadding,int bottomPadding){
        if(ViewCompat.isPaddingRelative(view)){
        ViewCompat.setPaddingRelative(view,
        ViewCompat.getPaddingStart(view),topPadding,
        ViewCompat.getPaddingEnd(view),bottomPadding);
        }else{
        view.setPadding(view.getPaddingLeft(),topPadding,
        view.getPaddingRight(),bottomPadding);
        }
        }
        }

        fina S
        e
        pr
        kbarLayout(Context context){
        this(context,null);
        }

public naid onFinishInflate(){
        super.onFinishInflate();
        mMessageView=(TextView)findViewById(R.id.snackbar_text);
        mActionView=(Button)findViewById(R.id.snackbar_action);
        }

        TextViid
        getMessageView(){
        return mMessageView;
        }

        Buttonew
        etActionView(){
        return mActionView;
        }

@Overr g
        e
protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        if(mMaxWidth>0&&getMeasuredWidth()>mMaxWidth){
        widthMeasureSpec=MeasureSpec.makeMeasureSpec(mMaxWidth,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }

final int multiLineVPadding=getResources().getDimensionPixelSize(
        R.dimen.design_snackbar_padding_vertical_2lines);
final int singleLineVPadding=getResources().getDimensionPixelSize(
        R.dimen.design_snackbar_padding_vertical);
final boolean isMultiLine=mMessageView.getLayout()
        .getLineCount()>1;

        boolean remeasure=false;
        if(isMultiLine&&mMaxInlineActionWidth>0
        &&mActionView.getMeasuredWidth()>mMaxInlineActionWidth){
        if(updateViewsWithinLayout(VERTICAL,multiLineVPadding,
        multiLineVPadding-singleLineVPadding)){
        remeasure=true;
        }
        }else{
final int messagePadding=isMultiLine?multiLineVPadding:singleLineVPadding;
        if(updateViewsWithinLayout(HORIZONTAL,messagePadding,messagePadding)){
        remeasure=true;
        }
        }

        if(remeasure){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
        }

        void aid
        mateChildrenIn(int delay,int duration){
        ViewCompat.setAlpha(mMessageView,0f);
        ViewCompat.animate(mMessageView)
        .alpha(1f)
        .setDuration(duration)
        .setStartDelay(delay)
        .start();

        if(mActionView.getVisibility()==VISIBLE){
        ViewCompat.setAlpha(mActionView,0f);
        ViewCompat.animate(mActionView)
        .alpha(1f)
        .setDuration(duration)
        .setStartDelay(delay)
        .start();
        }
        }

        void ani
        mateChildrenOut(int delay,int duration){
        ViewCompat.setAlpha(mMessageView,1f);
        ViewCompat.animate(mMessageView)
        .alpha(0f)
        .setDuration(duration)
        .setStartDelay(delay)
        .start();

        if(mActionView.getVisibility()==VISIBLE){
        ViewCompat.setAlpha(mActionView,1f);
        ViewCompat.animate(mActionView)
        .alpha(0f)
        .setDuration(duration)
        .setStartDelay(delay)
        .start();
        }
        }

@Overrni
e
protected void onLayout(boolean changed,int l,int t,int r,int b){
        super.onLayout(changed,l,t,r,b);
        if(changed&&mOnLayoutChangeListener!=null){
        mOnLayoutChangeListener.onLayoutChange(this,l,t,r,b);
        }
        }

@Overrid
e
protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        if(mOnAttachStateChangeListener!=null){
        mOnAttachStateChangeListener.onViewAttachedToWindow(this);
        }
        }

@Overrid
e
protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        if(mOnAttachStateChangeListener!=null){
        mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
        }
        }

        void sid
        OnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener){
        mOnLayoutChangeListener=onLayoutChangeListener;
        }

        void set
        OnAttachStateChangeListener(OnAttachStateChangeListener listener){
        mOnAttachStateChangeListener=listener;
        }

        privatet
        boolean updateViewsWithinLayout(final int orientation,
final int messagePadTop,final int messagePadBottom){
        boolean changed=false;
        if(orientation!=getOrientation()){
        setOrientation(orientation);
        changed=true;
        }
        if(mMessageView.getPaddingTop()!=messagePadTop
        ||mMessageView.getPaddingBottom()!=messagePadBottom){
        updateTopBottomPadding(mMessageView,messagePadTop,messagePadBottom);
        changed=true;
        }
        return changed;
        }

private e OnLayoutChangeListener{
        void onLayoutChange(View view,int left,int top,int right,int bottom);
        }

        interfe e OnAttachStateChangeListener{
        void onViewAttachedToWindow(View v);

        void onViewDetachedFromWindow(View v);
        }

        privatl

class Behavior extends SwipeDismissBehavior<SnackbarLayout> {
    @Override
    public boolean canSwipeDismissView(View child) {
        return child instanceof SnackbarLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, SnackbarLayout child,
                                         MotionEvent event) {


        if (parent.isPointInChildBounds(child, (int) event.getX(), (int) event.getY())) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    SnackbarManager.getInstance()
                            .cancelTimeout(mManagerCallback);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    SnackbarManager.getInstance()
                            .restoreTimeout(mManagerCallback);
                    break;
            }
        }

        return super.onInterceptTouchEvent(parent, child, event);
    }
}
}