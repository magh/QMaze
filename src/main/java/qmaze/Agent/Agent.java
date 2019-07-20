package qmaze.Agent;

import java.util.ArrayList;
import java.util.Iterator;

import qmaze.Environment.Coordinates;

/**
 * Q(S(t), A(t)) ← Q(S(t), A(t)) + α [ R(t+1) + γ max Q(S(t+1), a) − Q(S(t), A(t)) ].
 * 
 * @author katharine
 * I know about:
 *  - My memory of learned rewards and possible actions
 *  - My learning parameters
 * I am told about:
 *  - The surrounding open rooms.
 *  - If there is a reward in this room.
 *   and use them to make decisions about which room to go in next.
 * I don't know:
 *  - How many episodes I am trained for
 * I don't control:
 *  - My movements overall - instead I am told to move at each step
 *  and given information about the environment.
 */
public class Agent {
    
    private final AgentMemory memory;
    private AgentLearningParameters learningParameters;
    
    public Agent(AgentLearningParameters learningParameters) {
        this.learningParameters = learningParameters;
        this.memory = new AgentMemory();
    }
    
    public Coordinates location() {
        return memory.getCurrentState();
    }
    
    public void start(Coordinates startingState) {
        memory.setStartingState(startingState);
    }
    
    public void move(Coordinates nextState) {
        memory.move(nextState);
    }
        
    public Coordinates chooseAction(ArrayList<Coordinates> nextAvailableActions) throws NoWhereToGoException {
        if (nextAvailableActions.isEmpty()) {
            throw new NoWhereToGoException(memory.getCurrentState());
        } else {
            double useMemory = Math.random();
            Coordinates nextAction;
            if (useMemory < learningParameters.getEpsilon()) {
                nextAction = pickRandomAction(nextAvailableActions);
            } else {
                nextAction = pickBestActionOrRandom(nextAvailableActions);
            }

            return nextAction;
        }
    }
    
    public void takeAction(Coordinates actionTaken, double reward) {
        double currentQValue = this.memory.rewardFromAction(this.location(), actionTaken);
        double estimatedBestFutureReward = 0.0D;
        ArrayList<Coordinates> actionsForFutureState = this.memory.actionsForState(actionTaken);
        if (!actionsForFutureState.isEmpty()) {
            Coordinates max_reward_from_subequent_action = this.pickBestActionOrRandom(actionsForFutureState);
            estimatedBestFutureReward = this.memory.rewardFromAction(actionTaken, max_reward_from_subequent_action);
        }

        double alpha = this.learningParameters.getLearningRate();
        double gamma = this.learningParameters.getGamma();
        double qValue = alpha * (reward + gamma * estimatedBestFutureReward - currentQValue);
        this.memory.updateMemory(actionTaken, qValue);
        this.memory.move(actionTaken);
    }

    private Coordinates pickRandomAction(ArrayList<Coordinates> actions) {
        int options = actions.size();
        int choice = (int)(Math.random() * (double)options);
        return (Coordinates)actions.get(choice);
    }

    private Coordinates pickBestActionOrRandom(ArrayList<Coordinates> actions) {
        ArrayList<Coordinates> bestActions = new ArrayList();
        double highestReward = 0.0D;
        Iterator var5 = actions.iterator();

        while(var5.hasNext()) {
            Coordinates action = (Coordinates)var5.next();
            double rewardMemory = this.memory.rewardFromAction(this.location(), action);
            if (rewardMemory > highestReward) {
                highestReward = rewardMemory;
                bestActions = new ArrayList();
                bestActions.add(action);
            }

            if (rewardMemory == highestReward) {
                bestActions.add(action);
            }
        }

        return this.pickRandomAction(bestActions);
    }

    public AgentMemory getMemory() {
        return memory;
    }
    
    public AgentLearningParameters getLearningParameters() {
        return learningParameters;
    }
    
    public void setLearningParameters(AgentLearningParameters parameters) {
        this.learningParameters = parameters;
    }
    
    public void introduceSelf(Coordinates startingState) {
        double alpha = learningParameters.getLearningRate();
        double gamma = learningParameters.getGamma();
        double epsilon = learningParameters.getEpsilon();
        System.out.println("I'm training with epsilon: " + epsilon + " gamma: " 
                + gamma + " and alpha: " + alpha + "\nStaring at " + startingState.toString());
    }
    
}
