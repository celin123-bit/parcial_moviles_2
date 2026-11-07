package pe.edu.moivo.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Objects;
import pe.edu.moivo.R;
import pe.edu.moivo.databinding.ItemRowBinding;

public class RowAdapter extends ListAdapter<RowAdapter.Row, RowAdapter.Holder> {
    public interface OnClick { void onClick(long id); }
    private final OnClick onClick;
    public RowAdapter(OnClick onClick) {
        super(new DiffUtil.ItemCallback<Row>() {
            @Override public boolean areItemsTheSame(@NonNull Row a, @NonNull Row b) { return a.id == b.id && a.kind.equals(b.kind); }
            @Override public boolean areContentsTheSame(@NonNull Row a, @NonNull Row b) {
                return Objects.equals(a.title, b.title) && Objects.equals(a.subtitle, b.subtitle)
                    && Objects.equals(a.tag, b.tag) && Objects.equals(a.footer, b.footer);
            }
        });
        this.onClick = onClick;
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(ItemRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        Row row = getItem(position);
        holder.binding.tag.setText(row.tag); holder.binding.title.setText(row.title);
        holder.binding.subtitle.setText(row.subtitle); holder.binding.footer.setText(row.footer);
        boolean isPackage = "package".equals(row.kind);
        holder.binding.thumbnail.setVisibility(isPackage ? View.VISIBLE : View.GONE);
        if (isPackage) holder.binding.thumbnail.setImageResource(destinationImage(row.tag));
        holder.binding.getRoot().setOnClickListener(v -> onClick.onClick(row.id));
    }
    private static int destinationImage(String destination) {
        if ("CUSCO".equals(destination)) return R.drawable.photo_cusco;
        if ("ICA".equals(destination)) return R.drawable.photo_ica;
        if ("LIMA".equals(destination)) return R.drawable.photo_lima;
        return R.drawable.hero_andes;
    }
    static class Holder extends RecyclerView.ViewHolder {
        final ItemRowBinding binding;
        Holder(ItemRowBinding binding) { super(binding.getRoot()); this.binding = binding; Typography.apply(binding.getRoot()); }
    }
    public static class Row {
        final long id;
        final String kind, tag, title, subtitle, footer;
        public Row(long id, String kind, String tag, String title, String subtitle, String footer) {
            this.id = id; this.kind = kind; this.tag = tag; this.title = title; this.subtitle = subtitle; this.footer = footer;
        }
    }
}
