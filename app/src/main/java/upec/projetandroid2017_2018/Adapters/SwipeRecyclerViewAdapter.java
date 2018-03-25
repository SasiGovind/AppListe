package upec.projetandroid2017_2018.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;

import upec.projetandroid2017_2018.Lists.ItemData;
import upec.projetandroid2017_2018.R;

public class SwipeRecyclerViewAdapter extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapter.SimpleViewHolder> {

    private Context mContext;
    private ArrayList<ItemData> noteList;

    public SwipeRecyclerViewAdapter(Context context, ArrayList<ItemData> objects) {
        this.mContext = context;
        this.noteList = objects;
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_layout, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final ItemData item = noteList.get(position);
/*
        viewHolder.Name.setText(item.getName() + " - Row Position " + position);
        viewHolder.Info.setText(item.getInfo());


        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //dari kiri
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        //dari kanan
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wraper));

        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, " Click : " + item.getName() + " \n" + item.getInfo(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked on Information " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "Clicked on Share " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "Clicked on Edit  " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                noteList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, noteList.size());
                mItemManger.closeAllItems();
                Toast.makeText(v.getContext(), "Deleted " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
*/
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        public SwipeLayout swipeLayout;
        public TextView Name;
        public TextView Info;
        public ImageView listIcon;
        public TextView Delete;
        public TextView Edit;
        public TextView Share;
        public CheckBox checkBox;
        public ImageButton btnLocation;

        public ImageView favorite;
        public ImageView pinned;

        public LinearLayout mainSLayout;
        public LinearLayout extrasLayout;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            Name = (TextView) itemView.findViewById(R.id.Name);
            Info = (TextView) itemView.findViewById(R.id.Info);
            listIcon = (ImageView) itemView.findViewById(R.id.listIcon);
            Delete = (TextView) itemView.findViewById(R.id.Delete);
            Edit = (TextView) itemView.findViewById(R.id.Edit);
            Share = (TextView) itemView.findViewById(R.id.Share);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            btnLocation = (ImageButton) itemView.findViewById(R.id.btnLocation);

            favorite = (ImageView) itemView.findViewById(R.id.xFavorite);
            pinned = (ImageView) itemView.findViewById(R.id.xPin);

            mainSLayout = (LinearLayout) itemView.findViewById(R.id.mainSwipeLayout);
            extrasLayout = (LinearLayout) itemView.findViewById(R.id.extrasLayout);
        }
    }

    ////Fonction pour la recherche
    public void setFilters(ArrayList<ItemData> newlist){
        noteList.clear();
        noteList.addAll(newlist);
        this.notifyDataSetChanged();
    }
}
