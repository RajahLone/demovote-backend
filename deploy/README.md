- On debian setup: `apt install openjdk`, edit `demovote-backend.service` with correct paths in `ExecStart` and `WorkingDirectory`, and put it into `/etc/systemd/system/.


- `WorkingDirectory` will contain the src/main/ressources/: `application.properties`, `logs` subfolder, `*.p12` (certificates store) and others assets outside the .war file.


- You may use a static symbolic link to fix versions updates/changes in the .war filename, such as `unlink /<pathto>/demovote-backend.war && ln -s /<pathto>/demovote-backend-<version>.war /<pathto>/demovote-backend.war`.


- Files are stored in ../uploads/* with UUID names. Uploads happen in ../uploads-temp/(fileId)-filename/*
