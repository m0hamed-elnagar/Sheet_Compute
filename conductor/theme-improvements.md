# Theme and Contrast Improvements

## Objective
Refine the app's theme to improve visual contrast, replace harsh white backgrounds with softer tinted colors, and fix hardcoded colors in the calendar screen that do not adapt well to the modern theme.

## Key Files & Context
- `app/src/main/res/values/colors.xml`: Contains the core color palette.
- `app/src/main/res/layout/employee_attendance_fragment.xml`: Layout for the employee summary screen containing the status cards.
- `app/src/main/res/layout/fragment_holidays_calendar.xml`: Layout containing the `CalendarView`.
- `app/src/main/res/layout/custom_day_view.xml` & `calendar_month_header_layout.xml`: Layouts for the calendar cells and headers.

## Implementation Steps
1. **Apply High-Contrast Theme**: 
   - Update `colors.xml` with the WCAG AA compliant Navy Blue (`#1E3A8A`) and Teal (`#0D9488`) palette.   - Adjust semantic colors (Present, Absent, Late) to have better contrast against light backgrounds.
2. **Tinted Summary Cards**:
   - Instead of using a single `status_card_selector.xml` with a white normal state for all cards, define specific soft-tinted background selectors for each card type (e.g., soft green for Presents, soft red for Absents).
   - Apply these new selectors to the respective `MaterialCardView` elements in `employee_attendance_fragment.xml`.
3. **Fix Calendar Hardcoded Colors**:
   - In `fragment_holidays_calendar.xml`, remove `android:background="@color/white"` from the `CalendarView` so it blends with the surface background.
   - In `custom_day_view.xml`, change the `calendarDayText` color from `@android:color/black` to `@color/text_primary`.
   - In `calendar_month_header_layout.xml`, change the `monthTextView` color from `@color/black` to `@color/text_primary`.

## Verification
- Review the Employee Attendance screen to ensure the top summary cards have soft, pleasant background tints instead of stark white.
- Open the Calendar screen to verify the white background behind the calendar grid is gone, and the text colors adapt correctly to the theme.
- Verify overall contrast and readability across the app.