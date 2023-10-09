CREATE TABLE "top5_commodities_by_country" (
"Country" TEXT,
"Commodity" TEXT,
"Value (Dollars)" INTEGER
);

CREATE TABLE "top_weekday_for_commodity" (
"Commodity" TEXT,
"Weekday" TEXT,
"Value (Dollars)" INTEGER
);

CREATE TABLE "total_value_by_commodity" (
"Commodity" TEXT,
  "Value (Dollars)" INTEGER,
  "Value (Tonnes)" REAL
);

CREATE TABLE "total_value_by_country" (
"Country" TEXT,
  "Value (Dollars)" INTEGER,
  "Value (Tonnes)" REAL
);

CREATE TABLE "total_value_by_month" (
"Month" TEXT,
  "Value (Dollars)" INTEGER,
  "Value (Tonnes)" INTEGER
);

CREATE TABLE "total_value_by_month_top5" (
"Month" TEXT,
  "Value (Dollars)" REAL,
  "Value (Tonnes)" REAL
);

CREATE TABLE "total_value_by_transport_mode" (
"Transport_Mode" TEXT,
  "Value (Dollars)" REAL,
  "Value (Tonnes)" REAL
);

CREATE TABLE "total_value_by_weekday" (
"Weekday" TEXT,
  "Value (Dollars)" INTEGER,
  "Value (Tonnes)" INTEGER
);