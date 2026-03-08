package com.revature.revplay.entity;

/**
 * This enumeration defines the two primary authorization tiers within the
 * RevPlay platform.
 * It is used for role-based access control (RBAC) in the SecurityConfig to
 * determine which users can access the creator's upload tools and dashboards.
 * By using a simple, immutable enum, the system ensures that user permissions
 * are consistent across the entire application and easy to verify during
 * authentication.
 */
public enum Role {
    /**
     * Representing a standard listener who can browse, play music, and follow
     * artists.
     */
    USER,

    /**
     * Representing a professional creator who has access to the upload suite,
     * album management tools, and audience analytics dashboard.
     */
    ARTIST
}