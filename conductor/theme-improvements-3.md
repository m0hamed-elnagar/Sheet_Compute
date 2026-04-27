# UI Polish Part 3: Reverting Colors, Fixing Icons, Dropdowns, and Dark Mode

## Objective
Address the latest user feedback:
1. **Summary Buttons**: Revert to pastel backgrounds with vibrant content (icons/text) as the solid high-contrast look was rejected.
2. **Missing Icons**: "Presents" and "Extra" summary cards are missing icons.
3. **Dropdowns**: Fix the "too narrow" and "white in dark mode" issues.
4. **Holiday Items**: The holiday list items are white in dark mode; they must be dark.
5. **Calendar**: The calendar background is white in dark mode; it must be dark.

## Implementation Steps

### 1. Revert Summary Card Colors
- **Selectors**: Update `bg_present_selector.xml`, `bg_absent_selector.xml`, `bg_late_selector.xml`, and `bg_leave_selector.xml` to use:
    - Normal: `@color/bg_present_soft` (or equivalent pastel).
    - Selected/Pressed: `@color/bg_present_selected` (slightly darker pastel).
- **Layout**: In `employee_attendance_fragment.xml`:
    - Change `TextView` and `ImageView` colors back to their vibrant semantic colors (e.g., `@color/status_present`).
    - Keep `android:layout_gravity="center"` for vertical centering.

### 2. Fix Missing Icons
- In `employee_attendance_fragment.xml`:
    - **Presents**: Change icon to `@drawable/calendar_month_24dp_` or `@android:drawable/ic_menu_my_calendar`.
    - **Extra**: Change icon to `@android:drawable/ic_input_add`.

### 3. Fix Dropdowns (Spinners)
- In `employee_attendance_fragment.xml` and `fragment_date_filter.xml`:
    - **Width**: Ensure `android:layout_width="0dp"` with `android:layout_weight="1"` (or `match_parent`) is used correctly in their containers.
    - **Background**: Remove `android:background="@drawable/spinner_background"`. This caused the arrow to disappear and narrowed the width.
    - **Dark Mode**: Use `android:backgroundTint="@color/border"` or simply use a Material Spinner style to ensure it adapts to the theme.

### 4. Fix Holiday Item & Calendar Dark Mode
- **Holiday Item**: In `item_holiday.xml`:
    - Remove hardcoded `android:backgroundTint="@android:color/white"`.
    - Replace with `app:cardBackgroundColor="@color/card_normal_bg"`.
    - Update text colors from hardcoded black to `@color/text_primary`.
- **Calendar BG**: In `fragment_holidays_calendar.xml`:
    - Ensure the root or parent container uses `android:background="@color/background"`.
    - Ensure the `MaterialCardView` containing the calendar uses `app:cardBackgroundColor="@color/card_normal_bg"`.
- **Header**: In `calendar_month_header_layout.xml`, change the month title color from `@color/white` to `@color/text_primary`.

## Verification
- Verify summary cards are pastel with vibrant icons.
- Verify dropdowns are full width with arrows and correct colors in dark mode.
- Verify holiday items and calendar background are dark/slate in dark mode.