# UI/UX Enhancement Plan - Professional Refresh (Modern Data Analyst)

## Objective
Transform the app from a scaffold look to a professional, polished "Enterprise-grade" utility app by modernizing the color palette (Slate & Emerald), fixing the Top Bar, and aligning with Material 3 design principles for both Light and Dark modes.

## Key Files & Context
- `app/src/main/res/values/colors.xml`: Update to new professional palette (Light Mode).
- `app/src/main/res/values-night/themes.xml`: Implement Dark Mode theme.
- `app/src/main/res/values/themes.xml`: Align themes with Material 3 and new colors.
- `app/src/main/res/layout/activity_main.xml`: Clean up the Toolbar and fix the title.
- `app/src/main/res/values/strings.xml`: Update names and strings.

## Implementation Steps

### Phase 1: Color & Theme Update
1. **Redefine Colors (Light Mode)**:
   - **Primary (Stability):** `#0F172A` (Headers, key actions)
   - **Secondary (Sheet DNA):** `#10B981` (Success states, imports, FABs)
   - **Accent (Data Highlight):** `#3B82F6` (Selected cells, computed values)
   - **Background:** `#F8FAFC` (Clean canvas)
   - **Surface:** `#FFFFFF` (Cards, sheets)
   - **Border/Divider:** `#E2E8F0`
2. **Redefine Colors (Dark Mode)**:
   - **Primary:** `#020617`
   - **Secondary:** `#34D399`
   - **Accent:** `#60A5FA`
   - **Background:** `#020617`
   - **Surface:** `#1E293B`
   - **Border/Divider:** `#334155`
3. **Update Theme**:
   - Ensure full Material 3 compliance.
   - Set status bar to Primary and ensure text on toolbar is legible (White/Light).
   - Use `Theme.Material3.DayNight.NoActionBar` as the base.

### Phase 2: Top Bar (Toolbar) Modernization
1. **Refactor `activity_main.xml`**:
   - Remove the `MaterialCardView` wrapper around the title.
   - Use standard `app:title` or a simple `TextView` if custom alignment is needed.
   - Set toolbar background to `?attr/colorPrimary`.
   - Update branding text to "SheetCompute".
2. **Standardize Branding**:
   - Update `app_name` string.

### Phase 3: UI-Wide Refinements
1. **Consistent Cards**: Standardize `cardCornerRadius="12dp"` and `cardElevation="0dp"` (using `outline` or subtle borders for a modern look).
2. **Typography**: Use `Inter` or `Roboto` with appropriate weights for data precision.

## Verification & Testing
1. **Visual Verification**: Check the Toolbar alignment and coloring on a real device/emulator in both Light and Dark modes.
2. **Accessibility**: Verify contrast ratios for text on new backgrounds.
3. **Menu Interaction**: Ensure the Settings and Calendar icons in the toolbar are clearly visible and functional.
