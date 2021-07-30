package vn.poly.goodfood.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import vn.poly.goodfood.R;
import vn.poly.goodfood.dao.FoodDao;
import vn.poly.goodfood.model.AnVat;
import vn.poly.goodfood.model.MonPhu;
import vn.poly.goodfood.model.OrderFood;
import vn.poly.goodfood.mysqlite.MySql;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterAnVat extends BaseAdapter {
    Context context;
    List<AnVat>list;
    double tongTien=1;
    FirebaseDatabase database;
    DatabaseReference reference;
    String foodId="";
    FoodDao foodDao;
    AnVat anVat;

    public AdapterAnVat(Context context, List<AnVat> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder{
        ImageView imgAnh;
        TextView tvTen,tvGia;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_kfc,parent,false);
            holder.imgAnh = convertView.findViewById(R.id.img_anhKfc);
            holder.tvTen = convertView.findViewById(R.id.txt_tenKfc);
            holder.tvGia = convertView.findViewById(R.id.txt_giaKfc);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        Locale locale = new Locale("en","US");
        NumberFormat fmt =NumberFormat.getCurrencyInstance(locale);

        Picasso.with(context).load(list.get(position).getLinkAnh()).into(holder.imgAnh);
        holder.tvTen.setText(list.get(position).getNameAnh());
        holder.tvGia.setText("Giá: "+fmt.format(list.get(position).getDonGia()/100)+" VNĐ");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_them_vao_giohang);

                ImageView imgDimiss =dialog.findViewById(R.id.img_Dialog_dimiss);
                ImageView imgAnh = dialog.findViewById(R.id.img_Dialog_anhFood);
                TextView txtTen = dialog.findViewById(R.id.txt_Dialog_tenMon);
                TextView txtGia = dialog.findViewById(R.id.txt_Dialog_donGia);
                ElegantNumberButton soLuong = dialog.findViewById(R.id.numberButton_SoLuong);
                TextView txtTong = dialog.findViewById(R.id.txt_Dialog_TongTien);
                Button btnAdd = dialog.findViewById(R.id.btn_Dialog_ADD);

                AnVat anVat =list.get(position);
                Picasso.with(context).load(anVat.getLinkAnh()).into(imgAnh);
                txtTen.setText(anVat.getNameAnh());
                txtGia.setText("Giá: "+String.valueOf(anVat.getDonGia())+"VNĐ");
                tongTien = 1;
                tongTien = anVat.getDonGia() * Integer.parseInt(soLuong.getNumber());
                txtTong.setText(tongTien+"VNĐ");
                soLuong.setOnClickListener(new ElegantNumberButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tongTien = 1;
                        tongTien = anVat.getDonGia() * Integer.parseInt(soLuong.getNumber());
                        txtTong.setText(tongTien+"VNĐ");
                    }
                });
                database =FirebaseDatabase.getInstance();
                reference =database.getReference("ANVAT");

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foodDao = new FoodDao(context);
                        OrderFood orderFood = new OrderFood(txtTen.getText().toString(),Integer.parseInt(soLuong.getNumber()),anVat.getDonGia());
                        if (foodDao.insertOderFood(orderFood) >0){
                            Toast.makeText(context, "Thêm giỏ hàng thành công !!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Thêm thất bại ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                imgDimiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return convertView;
    }
}
