package rafael.ninoles.parra.dailyit.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.exceptions.InputNeededException;
import rafael.ninoles.parra.dailyit.model.Category;
import rafael.ninoles.parra.dailyit.enums.RequestStatus;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;
import rafael.ninoles.parra.dailyit.utilities.Colors;

/**
 * Dialog to show info about a category. It allows to create or modify a category.
 */
public class NewCategoryDialog {
    private final Context context;
    private final Activity activity;
    private final EditText etCategoryName;
    private final TextView tvDialogTitle;
    private final Spinner spinner;
    private final Button btSave;
    private final Button btCancel;
    private final View categoryColor;
    private final ProgressBar dialogLoader;
    private final Dialog dialog;
    private String id;
    private Category category;

    public NewCategoryDialog(Context context, Activity activity, String id) {
        this.context = context;
        this.activity = activity;
        this.id = id;
        this.dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_category_dialog);

        etCategoryName = dialog.findViewById(R.id.etCategoryName);
        tvDialogTitle = dialog.findViewById(R.id.tvCategoryDialogTitle);
        spinner = dialog.findViewById(R.id.spCategorySelected);
        btSave = dialog.findViewById(R.id.btSave);
        btCancel = dialog.findViewById(R.id.btCancel);
        categoryColor = dialog.findViewById(R.id.categoryColor);
        dialogLoader = dialog.findViewById(R.id.dialogLoader);
        if(id==null){
            tvDialogTitle.setText(context.getString(R.string.new_category));
        }else{
            tvDialogTitle.setText(context.getString(R.string.loading));
        }

        btCancel.setOnClickListener(e->{
            dialog.dismiss();
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryColor.setBackgroundColor(Colors.getColor(adapterView.getAdapter().getItem(i).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btSave.setOnClickListener(e->{
            save();
        });

        dialog.show();
        loadData();
    }

    public NewCategoryDialog(Context context, Activity activity){
        this(context,activity,null);
    }

    private void loadData() {
        if(id==null){
            dialogLoader.setVisibility(View.GONE);
            return;
        }else{
            DailyItRepository.getInstance().getCategorieById(FirebaseAuth.getInstance().getUid(), id)
            .observe((LifecycleOwner) activity, category -> {
                System.out.println(Locale.getDefault().getLanguage());

                if(category.getId().equals("Work")){
                    if(category.getName().equals("Work")){
                        etCategoryName.setText(context.getString(R.string.work));
                    }else{
                        etCategoryName.setText(category.getName());
                    }
                    tvDialogTitle.setText(String.format(context.getString(R.string.editing),context.getString(R.string.work)));
                }else{
                    tvDialogTitle.setText(String.format(context.getString(R.string.editing),category.getName()));
                }
                String compareValue = Colors.getFromDBToLocale(category.getColor());
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.available_color, android.R.layout.simple_spinner_item);
                int spinnerPosition = adapter.getPosition(compareValue);
                if(spinnerPosition>=0){
                    spinner.setSelection(spinnerPosition);
                }
                dialogLoader.setVisibility(View.GONE);
            });
        }
    }

    private void save() {
        try {
            checkInputs();
        }catch (InputNeededException e){
            Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
            return;
        }
        if(id==null){
            saveNewTask();
        }else{
            category = new Category();
            category.setId(id);
            category.setName(etCategoryName.getText().toString());
            category.setColor(Colors.getColorInDB(spinner.getSelectedItem().toString()));
            dialogLoader.setVisibility(View.VISIBLE);
            DailyItRepository.getInstance().updateCategory(category, FirebaseAuth.getInstance().getUid())
                    .observe((LifecycleOwner) activity, (Observer<RequestStatus>) requestStatus -> {
                        showSavedCorrectly();
                    });
        }
    }

    private void showSavedCorrectly() {
        dialogLoader.setVisibility(View.GONE);
        Toast toast = Toast.makeText(context, context.getString(R.string.category_saved), Toast.LENGTH_SHORT);
        toast.show();
        dialog.dismiss();
    }

    private void saveNewTask() {
        category = new Category();
        category.setName(etCategoryName.getText().toString());
        category.setColor(Colors.getColorInDB(spinner.getSelectedItem().toString()));
        dialogLoader.setVisibility(View.VISIBLE);
        DailyItRepository.getInstance().createCategory(category, FirebaseAuth.getInstance().getUid())
                .observe((LifecycleOwner) activity, (Observer<RequestStatus>) requestStatus -> {
                    showSavedCorrectly();
                });
    }

    private void checkInputs() throws InputNeededException {
        if(etCategoryName.getText().toString().isEmpty()){
            throw new InputNeededException(context.getString(R.string.no_category_name));
        }
    }
}
