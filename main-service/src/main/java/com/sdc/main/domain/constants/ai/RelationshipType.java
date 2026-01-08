package com.sdc.main.domain.constants.ai;

import lombok.Getter;

/**
 * User relationship type with contact.
 * @since 11.2025
 */
@Getter
public enum RelationshipType {
    /**
     * Supervisor.
     */
    SUPERVISOR,

    /**
     * Colleague.
     */
    COLLEAGUE,

    /**
     * Employee.
     */
    EMPLOYEE,

    /**
     * Customer.
     */
    CUSTOMER,

    /**
     * Lead.
     */
    LEAD,

    /**
     * Supplier.
     */
    SUPPLIER,

    /**
     * Business partner.
     */
    BUSINESS_PARTNER,

    /**
     * Investor.
     */
    INVESTOR,

    /**
     * Friend.
     */
    FRIEND,

    /**
     * Close friend.
     */
    CLOSE_FRIEND,

    /**
     * Family member.
     */
    FAMILY_MEMBER,

    /**
     * Mentor.
     */
    MENTOR,

    /**
     * Team.
     */
    TEAM,

    /**
     * Student.
     */
    STUDENT,

    /**
     * Vip client.
     */
    VIP_CLIENT
}
