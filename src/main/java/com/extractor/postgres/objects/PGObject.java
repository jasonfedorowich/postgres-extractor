package com.extractor.postgres.objects;

import java.util.Map;

public interface PGObject {

    boolean exists();

    PGObject create();

    PGObject drop();

}
