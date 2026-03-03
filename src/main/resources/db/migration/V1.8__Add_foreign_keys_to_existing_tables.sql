-- Alter existing 'trip_progress' table to reference 'jpl' and 'station'
ALTER TABLE trip_progress 
  ADD CONSTRAINT fk_trip_progress_jpl FOREIGN KEY (jpl_id) REFERENCES jpl(id),
  ADD CONSTRAINT fk_trip_progress_station FOREIGN KEY (station_id) REFERENCES station(id);

-- Alter existing 'warning' table to reference 'jpl'
ALTER TABLE warning 
  ADD CONSTRAINT fk_warning_jpl FOREIGN KEY (jpl_id) REFERENCES jpl(id);
