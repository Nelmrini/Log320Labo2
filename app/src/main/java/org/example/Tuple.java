package org.example;

/**
 * Simple tuple qui peux contenir deux valeurs de deux type distint
 *
 * @author Brian Normant
 */
public record Tuple<T1, T2>(T1 first, T2 second) {};
