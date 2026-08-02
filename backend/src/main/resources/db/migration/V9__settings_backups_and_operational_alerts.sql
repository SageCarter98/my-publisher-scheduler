create table system_settings (
  id uuid primary key,
  organization_id uuid not null references organization(id),
  setting_key varchar(120) not null,
  setting_value text not null,
  value_type varchar(30) not null default 'STRING',
  description varchar(500),
  updated_by uuid references app_user(id),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  version bigint not null default 0,
  constraint uk_system_settings_org_key unique (organization_id, setting_key)
);
create index idx_system_settings_org on system_settings(organization_id, setting_key);

create table backup_jobs (
  id uuid primary key,
  organization_id uuid not null references organization(id),
  backup_type varchar(30) not null,
  status varchar(30) not null,
  requested_by uuid references app_user(id),
  storage_location varchar(1000),
  checksum varchar(128),
  size_bytes bigint,
  message varchar(2000),
  requested_at timestamptz not null default now(),
  started_at timestamptz,
  completed_at timestamptz
);
create index idx_backup_jobs_org_requested on backup_jobs(organization_id, requested_at desc);

create table operational_alerts (
  id uuid primary key,
  organization_id uuid references organization(id),
  alert_type varchar(80) not null,
  severity varchar(20) not null,
  status varchar(20) not null,
  title varchar(250) not null,
  details text,
  source varchar(120) not null,
  detected_at timestamptz not null default now(),
  acknowledged_at timestamptz,
  acknowledged_by uuid references app_user(id),
  resolved_at timestamptz
);
create index idx_operational_alerts_status on operational_alerts(status, severity, detected_at desc);
