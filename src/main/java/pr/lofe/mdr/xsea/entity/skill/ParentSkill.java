package pr.lofe.mdr.xsea.entity.skill;

import org.bukkit.NamespacedKey;

public class ParentSkill extends Skill {

    private final NamespacedKey key;
    private final String description, name;

    private Skill[] skills;

    public ParentSkill(NamespacedKey key, String description, String name) {
        this.key = key;
        this.description = description;
        this.name = name;
    }

    public void addSkills(Skill... skills) {
        this.skills = skills;
    }

    public Skill[] getSkills() {
        return skills;
    }

    public Skill getSkill(NamespacedKey key) {
        for (Skill skill: skills) {
            if(skill.key().toString().equals(key.toString())) return skill;
        }
        return null;
    }

    public Skill getPrev(Skill skill) {
        for (int i = 1; i < skills.length; i++) {
            if(skills[i] == skill) return skills[i - 1];
        }
        return null;
    }

    public Skill getSkill(int index) {
        return skills[index];
    }

    @Override
    public boolean isInit() {
        return true;
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int currency() {
        return 2;
    }

}
