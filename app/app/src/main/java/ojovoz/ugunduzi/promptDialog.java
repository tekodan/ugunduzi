package ojovoz.ugunduzi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Eugenio on 08/03/2018.
 */
public abstract class promptDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {
    private final EditText input1;
    private final EditText input2;
    private final int type;

    /**
     * @param context
     * @param title resource id
     * @param message resource id
     */
    public promptDialog(Context context, int rType, int title, int message, String defaultText1, String defaultText2) {

        super(context);

        String title1="", title2="";

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0,20,0,0);

        type=rType;

        if(type==0) {
            setTitle(title);
            setMessage(message);
        } else {
            setMessage(message);
            //String[] titleParts=Integer.toString(title).split(",");
            String[] titleParts=context.getResources().getString(title).split(",");
            title1=titleParts[0];
            title2=titleParts[1];
            TextView tv = new TextView(context);
            tv.setText(title1);
            tv.setTextSize(20);
            tv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            tv.setPadding(10,10,10,10);
            layout.addView(tv,layoutParams);
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view1 = inflater.inflate( R.layout.edit_text_template, null );
        input1 = (EditText)view1.findViewById(R.id.myEditText);
        input1.setSingleLine();
        input1.setText(defaultText1);
        input1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                input1.selectAll();
            }
        });

        layout.addView(input1);

        if(type==1){
            input1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            TextView tv = new TextView(context);
            tv.setText(title2);
            tv.setTextSize(20);
            tv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            tv.setPadding(10,0,0,0);
            layout.addView(tv,layoutParams);
            View view2 = inflater.inflate( R.layout.edit_text_template, null );
            input2 = (EditText)view2.findViewById(R.id.myEditText);
            input2.setSingleLine();
            input2.setText(defaultText2);
            input2.setInputType(InputType.TYPE_CLASS_NUMBER);
            input2.setImeOptions(EditorInfo.IME_ACTION_DONE);
            input2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    input2.selectAll();
                }
            });

            layout.addView(input2,layoutParams);
            setCancelable(false);
        } else {
            input1.setImeOptions(EditorInfo.IME_ACTION_DONE);
            input2 = new EditText(context);
            setNegativeButton(R.string.cancelButtonText, this);
        }

        setView(layout);
        setPositiveButton(R.string.okButtonText, this);

    }

    /**
     * will be called when "cancel" pressed.
     * closes the dialog.
     * can be overridden.
     * @param dialog
     */
    public void onCancelClicked(DialogInterface dialog) {
        dialog.dismiss();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if(type==0) {
                if (onOkClicked(input1.getText().toString())) {
                    dialog.dismiss();
                }
            } else if(type==1) {
                String val1 = input1.getText().toString();
                String val2 = input2.getText().toString();
                if (onOkClicked(val1 + ";" + val2)) {
                    dialog.dismiss();
                }
            }
        } else {
            if(type==0) {
                onCancelClicked(dialog);
            }
        }
        create();
    }

    /**
     * called when "ok" pressed.
     * @param input
     * @return true, if the dialog should be closed. false, if not.
     */
    abstract public boolean onOkClicked(String input);


}