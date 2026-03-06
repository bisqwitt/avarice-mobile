package com.avaricious.effects.particle;

import com.avaricious.components.slot.SlotMachine;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.RunnableDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParticleManager {

    private static ParticleManager instance;

    public static ParticleManager I() {
        return instance == null ? instance = new ParticleManager() : instance;
    }

    private ParticleManager() {
    }

    // TODO use layering

    private final Map<ParticleEffect, ZIndex> particleEffects = new HashMap<>();

    public void draw(SpriteBatch batch, float delta) {
        for (Map.Entry<ParticleEffect, ZIndex> entry : particleEffects.entrySet()) {
            entry.getKey().update(delta);

            Pencil.I().addDrawing(new RunnableDrawing(
                () -> entry.getKey().draw(batch),
                entry.getValue()
            ));
        }
        Set<ParticleEffect> dump = new HashSet<>();
        for (ParticleEffect particleEffect : particleEffects.keySet()) {
            if (particleEffect.isComplete()) dump.add(particleEffect);
        }

        particleEffects.remove(dump);
    }

    public void create(float x, float y, ParticleType type, float scale, float emissionHigh, ZIndex layer) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(scale);
//        particle.setDuration(1);
        for (ParticleEmitter emitter : particle.getEmitters()) {
            emitter.getEmission().setHigh(emissionHigh);
        }

        particle.setPosition(x + SlotMachine.CELL_W / 2, y + SlotMachine.CELL_H / 2);
        particle.start();
        particleEffects.put(particle, layer);
    }

    public void create(float x, float y, ParticleType type, float scale, ZIndex layer) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(scale);

        particle.setPosition(x + SlotMachine.CELL_W / 2, y + SlotMachine.CELL_H / 2);
        particle.start();
        particleEffects.put(particle, layer);
    }

}

