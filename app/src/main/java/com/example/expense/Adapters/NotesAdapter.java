package com.example.expense.Adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense.Model.NotesModel;
import com.example.expense.R;
import com.example.expense.Tools.DBHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    Context context;
    List<NotesModel> listNotes;
    DBHelper dbHelper;

    // Constructor để khởi tạo context và danh sách ghi chú
    public NotesAdapter(Context context, List<NotesModel> listNotes) {
        this.context = context;
        this.listNotes = listNotes;
    }

    @NonNull
    @Override
    // Tạo ViewHolder từ layout item_notes, đây là phần giao diện của một phần tử trong RecyclerView
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_notes, parent, false);
        return new NotesAdapter.ViewHolder(view);
    }

    @Override
    // Liên kết dữ liệu với các view trong ViewHolder, khi item được hiển thị trong RecyclerView
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        holder.description.setText(listNotes.get(position).getNote_Description());
        holder.date.setText(listNotes.get(position).getNote_date() + ", " + listNotes.get(position).getNote_DateMonthYear() + " . " + listNotes.get(position).getNote_WeekDay());

        // Khi người dùng nhấn vào item ghi chú, hiển thị popup với các tùy chọn sửa hoặc xóa
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupOptions(holder, holder.getAdapterPosition());
            }
        });
    }

    // Hiển thị một popup khi người dùng nhấn vào ghi chú, với các tùy chọn chỉnh sửa và xóa
    private void popupOptions(ViewHolder holder, int adapterPosition) {
        Dialog dialog;
        dialog = new Dialog(context);

        dialog.setContentView(R.layout.item_options); // Giao diện popup
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);

        // Lấy các thành phần giao diện từ popup
        ImageView edit = dialog.findViewById(R.id.imageView5);
        ImageView delete = dialog.findViewById(R.id.imageView17);

        // Khi nhấn vào biểu tượng sửa, gọi hàm chỉnh sửa item
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editItem(holder, adapterPosition);
                dialog.dismiss(); // Đóng popup
            }
        });

        // Khi nhấn vào biểu tượng xóa, gọi hàm xóa item
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(adapterPosition);
                dialog.dismiss(); // Đóng popup
            }
        });

        dialog.show(); // Hiển thị popup
    }

    // Xóa ghi chú từ cơ sở dữ liệu và cập nhật lại danh sách
    private void deleteItem(int adapterPosition) {
        dbHelper = new DBHelper(context);
        dbHelper.deletefromNotes(listNotes.get(adapterPosition).getNote_ID()); // Xóa trong cơ sở dữ liệu
        listNotes.remove(adapterPosition); // Xóa khỏi danh sách
        notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

    // Sửa ghi chú bằng cách hiển thị dialog chỉnh sửa
    private void editItem(ViewHolder holder, int adapterPosition) {
        dbHelper = new DBHelper(context);
        final String[] Selected = new String[]{"January", "February", "March", "April", "May", "June", "July", "Augest", "September", "October", "November", "December"};

        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.item_addnote); // Giao diện chỉnh sửa ghi chú
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);

        // Lấy các thành phần giao diện từ dialog chỉnh sửa
        TextView daynumber = dialog.findViewById(R.id.day);
        TextView monthyear = dialog.findViewById(R.id.textView7);
        TextView daytext = dialog.findViewById(R.id.textView8);
        MaterialCardView dateCard = dialog.findViewById(R.id.dateCard);
        MaterialCardView addNote = dialog.findViewById(R.id.addnote);
        MaterialCardView cancelNote = dialog.findViewById(R.id.cancelnote);
        TextInputEditText notedescription = dialog.findViewById(R.id.notedescription);

        // Điền thông tin hiện tại vào các view
        notedescription.setText(listNotes.get(adapterPosition).getNote_Description());
        daynumber.setText(listNotes.get(adapterPosition).getNote_date());
        monthyear.setText(listNotes.get(adapterPosition).getNote_DateMonthYear());
        daytext.setText(listNotes.get(adapterPosition).getNote_WeekDay());

        // Chọn ngày tháng khi nhấn vào MaterialCardView
        dateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mYear, mMonth, mDay;
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(dialog.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                int numberMonth = selectedDate.get(Calendar.MONTH);
                                int numberday = selectedDate.get(Calendar.DATE);
                                int numberyear = selectedDate.get(Calendar.YEAR);
                                daynumber.setText("" + numberday);
                                monthyear.setText(Selected[numberMonth] + " " + numberyear);
                                switch (selectedDate.get(Calendar.DAY_OF_WEEK)) {
                                    case Calendar.SATURDAY:
                                        daytext.setText("Saturday");
                                        break;
                                    case Calendar.MONDAY:
                                        daytext.setText("Monday");
                                        break;
                                    case Calendar.TUESDAY:
                                        daytext.setText("Tuesday");
                                        break;
                                    case Calendar.WEDNESDAY:
                                        daytext.setText("Wednesday");
                                        break;
                                    case Calendar.THURSDAY:
                                        daytext.setText("Thursday");
                                        break;
                                    case Calendar.FRIDAY:
                                        daytext.setText("Friday");
                                        break;
                                    case Calendar.SUNDAY:
                                        daytext.setText("Sunday");
                                        break;
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // Thêm ghi chú vào cơ sở dữ liệu khi nhấn nút "Add Note"
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notedescription.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Note cannot be empty", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    dbHelper.editNote(listNotes.get(adapterPosition).getNote_ID(), daynumber.getText().toString(), notedescription.getText().toString(), monthyear.getText().toString(), daytext.getText().toString());
                    holder.description.setText(notedescription.getText().toString());
                    holder.date.setText(daynumber.getText().toString() + ", " + monthyear.getText().toString() + " . " + daytext.getText().toString());

                    listNotes.get(adapterPosition).setNote_Description(notedescription.getText().toString());
                    listNotes.get(adapterPosition).setNote_DateMonthYear(monthyear.getText().toString());
                    listNotes.get(adapterPosition).setNote_WeekDay(daytext.getText().toString());
                    listNotes.get(adapterPosition).setNote_date(daynumber.getText().toString());
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        // Hủy chỉnh sửa và đóng dialog
        cancelNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    // Trả về số lượng ghi chú trong danh sách
    public int getItemCount() {
        return listNotes.size();
    }

    // Lớp ViewHolder giữ các thành phần giao diện của mỗi item ghi chú trong RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, description;
        MaterialCardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txt_date);
            description = itemView.findViewById(R.id.txt_note);
            layout = itemView.findViewById(R.id.item_notes_layout);
        }
    }
}