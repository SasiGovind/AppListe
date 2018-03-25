package upec.projetandroid2017_2018.Lists;

import java.io.Serializable;

import upec.projetandroid2017_2018.R;

public class ItemData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String info;
    private String category;
    private String visibility;
    private int color;
    private int listIcon;
    private int favoris;
    private int pin;
    private int fait;
    private int reminder;
    private long reminderTime;

    public ItemData() {
    }

    public ItemData(String name, String info, String category, String visibility, int color, int listIcon, int favoris, int pin, int fait, int reminder, long reminderTime) {
        this.name = name;
        this.info = info;
        this.category = category;
        this.visibility = visibility;
        this.color = color;
        this.listIcon = listIcon;
        this.favoris = favoris;
        this.pin = pin;
        this.fait = fait;
        this.reminder = reminder;
        this.reminderTime = reminderTime;
    }

    public int getReminder() {
        return reminder;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public String getCategory() {
        return category;
    }

    public int getListIcon() {
        return listIcon;
    }

    public int getFavoris() {
        return favoris;
    }

    public int getPin() {
        return pin;
    }

    public int getFait() {
        return fait;
    }

    public void setFait(int fait) {
        this.fait = fait;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
