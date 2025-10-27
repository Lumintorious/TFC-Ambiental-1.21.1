package com.lumintorious.tfcambiental.data;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TemperatureModifier implements Comparable<TemperatureModifier>
{
    private String unlocalizedName;
    private float change;
    private float potency;
    private float wetness;
    private int count = 1;
    private float multiplier = 1f;

    public float getChange() {
        return change * multiplier;
    }

    public void setChange(float change) {
        this.change = change;
    }

    public float getPotency() {
        return potency * multiplier;
    }

    public void setPotency(float potency) {
        this.potency = potency;
    }

    public float getWetness() {
        return wetness;
    }

    public void setWetness(float wetness) {
        this.wetness = wetness;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    public TemperatureModifier(String unlocalizedName) {
        this(unlocalizedName, 0f, 0f, 0f);
    }

    public TemperatureModifier(String unlocalizedName, float change, float potency) {
        this(unlocalizedName, change, potency, 0f);
    }

    public TemperatureModifier(String unlocalizedName, float change, float potency, float wetness) {
        this.unlocalizedName = unlocalizedName;
        this.change = change;
        this.potency = potency;
        this.wetness = wetness;
    }

    public static Optional<TemperatureModifier> defined(String unlocalizedName, float change, float potency) {
        return Optional.of(new TemperatureModifier(unlocalizedName, change, potency, 0));
    }

    public static Optional<TemperatureModifier> defined(String unlocalizedName, float change, float potency, float wetness) {
        return Optional.of(new TemperatureModifier(unlocalizedName, change, potency, wetness));
    }

    public static Optional<TemperatureModifier> none() {
        return Optional.empty();
    }

    @Override
    public int compareTo(@NotNull TemperatureModifier o) {
        return Float.compare(Math.abs(this.change), Math.abs(o.change));
    }

    public static class Cache implements Iterable<TemperatureModifier>
    {
        private List<TemperatureModifier> list = new LinkedList<>();

        public Cache keepOnlyNEach(int n) {
            var grouped = list.stream().collect(Collectors.groupingBy(TemperatureModifier::getUnlocalizedName));
            this.list = grouped.entrySet().stream().flatMap(entry -> entry.getValue().stream().sorted(Comparator.reverseOrder()).limit(n)).toList();
            return this;
        }

        public void add(TemperatureModifier value) {
            if (value == null) {
                return;
            }
            list.add(value);
        }

        public void add(Optional<TemperatureModifier> TemperatureModifier) {
            TemperatureModifier.ifPresent(mod -> list.add(mod));
        }

        public float getTargetTemperature() {
            float change = 1f;
            for (var mod : list) {
                change += mod.getChange();
            }
            return change;
        }

        public float getTotalPotency() {
            float potency = 1.1f;
            for (var mod : list) {
                potency += mod.getPotency();
            }
            return potency;
        }

        public float getTargetWetness() {
            float wetness = 0f;
            for (var mod : list) {
                wetness += mod.getWetness();
            }
            return wetness;
        }

        public void forEach(Consumer<? super TemperatureModifier> func) {
            list.forEach(func);
        }

        @Override
        public Iterator<TemperatureModifier> iterator() {
            return list.iterator();
        }
    }

}
