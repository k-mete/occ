package org.agora.occ.enums;

/**
 * Represents the operational status of a network device or physical asset,
 * such as a JPL station or a locomotive.
 */
public enum ActiveStatus {

    /** The asset is fully operational and connected. */
    ACTIVE,

    /** The asset is powered off or intentionally disabled. */
    INACTIVE,

    /**
     * The asset is temporarily offline for scheduled or unscheduled maintenance.
     */
    MAINTENANCE
}
