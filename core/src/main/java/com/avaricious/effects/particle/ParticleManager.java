package com.avaricious.effects.particle;

import com.avaricious.components.slot.SlotMachine;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParticleManager {

    private static ParticleManager instance;

    public static ParticleManager I() {
        return instance == null ? instance = new ParticleManager() : instance;
    }

    private ParticleManager() {
    }

    private final Map<ParticleEffect, Color> particleEffects = new HashMap<>();
    private final List<ParticleEffect> behindCardEmitters = new ArrayList<>();
    private final List<ParticleEffect> topLayerEmitters = new ArrayList<>();

    public void draw(SpriteBatch batch, float delta) {
        for (Map.Entry<ParticleEffect, Color> entry : particleEffects.entrySet()) {
            entry.getKey().update(delta);

            batch.setColor(entry.getValue());
            Pencil.I().drawInColor(batch, entry.getValue(),
                () -> entry.getKey().draw(batch));
        }
        Set<ParticleEffect> dump = new HashSet<>();
        for (ParticleEffect particleEffect : particleEffects.keySet()) {
            if (particleEffect.isComplete()) dump.add(particleEffect);
        }

        particleEffects.remove(dump);
    }

    public void drawTopLayer(SpriteBatch batch, float delta) {
        for (ParticleEffect particleEffect : topLayerEmitters) {
            particleEffect.update(delta);
            particleEffect.draw(batch);
        }
        Set<ParticleEffect> dump = new HashSet<>();
        for (ParticleEffect particleEffect : topLayerEmitters) {
            if (particleEffect.isComplete()) dump.add(particleEffect);
        }
        topLayerEmitters.remove(dump);
    }

    public void drawBehindCardLayer(SpriteBatch batch, float delta) {
        for (ParticleEffect particleEffect : behindCardEmitters) {
            particleEffect.update(delta);
            particleEffect.draw(batch);
        }
        Set<ParticleEffect> dump = new HashSet<>();
        for (ParticleEffect particleEffect : behindCardEmitters) {
            if (particleEffect.isComplete()) dump.add(particleEffect);
        }
        behindCardEmitters.remove(dump);
    }

    public void create(float x, float y, ParticleType type, float streak, Color color) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.03f);
        particle.setDuration(1);
        for (ParticleEmitter emitter : particle.getEmitters()) {
            emitter.getEmission().setHigh(25 + streak * 5);
        }

        particle.setPosition(x + SlotMachine.CELL_W / 2, y + SlotMachine.CELL_H / 2);
        particle.start();
        particleEffects.put(particle, color);
    }

    public void createTopLayer(float x, float y, ParticleType type) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.05f);

        particle.setPosition(x, y);
        particle.start();
        topLayerEmitters.add(particle);
    }

    public void createBehindCardLayer(float x, float y, ParticleType type) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.03f);

        particle.setPosition(x, y);
        particle.start();
        behindCardEmitters.add(particle);
    }

}

